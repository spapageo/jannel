/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Spyridon Papageorgiou
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.spapageo.jannel.windowing;

import com.google.common.util.concurrent.ListenableFuture;
import io.netty.util.HashedWheelTimer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Window<K,R,P> {

    private final ConcurrentHashMap<K, DeferredRequest<K, R, P>> futures;

    private final InterruptingSemaphore availableSlots;

    private final HashedWheelTimer wheelTimer;

    private final int maxSize;

    public Window(@Nonnegative int size) {
        checkArgument(size > 0, "size must be > 0");

        this.futures = new ConcurrentHashMap<>(size*2);
        this.wheelTimer = new HashedWheelTimer();
        this.availableSlots = new InterruptingSemaphore(size);
        this.maxSize = size;

        wheelTimer.start();
    }

    @Nonnegative public int getMaxSize() {
        return this.maxSize;
    }

    @Nonnegative public int getSize() {
        return this.futures.size();
    }
    
    @Nonnegative public int getFreeSize() {
        return this.maxSize - this.futures.size();
    }
    
    public boolean containsKey(@Nonnull K key) {
        return this.futures.containsKey(checkNotNull(key));
    }
    
    @Nullable public ListenableFuture<P> get(K key) {
        return this.futures.get(checkNotNull(key));
    }
    

    public synchronized void destroy() {
        this.availableSlots.drainPermits();
        this.availableSlots.tryInterrupt();
        this.cancelAll();
        this.wheelTimer.stop();
    }

    @Nonnull public WindowFuture<R, P> offer(@Nonnull K key, @Nonnull R request, @Nonnegative long offerTimeoutMillis)
            throws InterruptedException {
        return this.offer(key, request, offerTimeoutMillis, -1);
    }

    @Nonnull public WindowFuture<R, P> offer(@Nonnull K key, @Nonnull R request, @Nonnegative long offerTimeoutMillis, long expireTimeoutMillis)
            throws InterruptedException {
        checkArgument(offerTimeoutMillis >= 0, "offerTimeoutMillis must be >= 0 ");
        checkNotNull(key);
        checkNotNull(request);

        if (!availableSlots.tryAcquire(offerTimeoutMillis, TimeUnit.MILLISECONDS)) {
            final DeferredRequest<K, R, P> failedRequest = DeferredRequest.create(key, request, this);
            failedRequest.setException(new TimeoutException());
            return failedRequest;
        }

        @Nonnull final DeferredRequest<K, R, P> future = expireTimeoutMillis < 1 ?
                                                      DeferredRequest.create(key, request, this) :
                                                      TimedDeferredRequest.create(key,
                                                                                  request,
                                                                                  this,
                                                                                  wheelTimer,
                                                                                  expireTimeoutMillis);
        @Nullable final DeferredRequest<K, R, P> previousDeferred = this.futures.putIfAbsent(key, future);

        if(previousDeferred != null){
            //The key already existed in the map
            availableSlots.release();
            final DeferredRequest<K, R, P> failedRequest = DeferredRequest.create(checkNotNull(key), checkNotNull(request), this);
            failedRequest.setException(new DuplicateKeyException("The key already exists in the window"));
            return failedRequest;
        }

        return future;
    }
    

    @Nonnegative public int getPendingOfferCount() {
        return this.availableSlots.getQueueLength();
    }
    

    @Nullable public WindowFuture<R, P> complete(K key, P response)  {

        // try to remove future from window
        final DeferredRequest<K, R, P> future = this.futures.remove(checkNotNull(key));
        if (future == null) {
            return null;
        }

        // signal that a future is completed
        this.availableSlots.release();

        future.set(checkNotNull(response));

        return future;
    }

    @Nullable public WindowFuture<R, P> fail(@Nonnull K key, @Nonnull Throwable t){

        // try to remove future from window
        final DeferredRequest<K, R, P> future = this.futures.remove(checkNotNull(key));
        if (future == null) {
            return null;
        }

        // signal that a future is completed
        this.availableSlots.release();

        // set failed
        future.setException(checkNotNull(t));

        return future;
    }
    

    @Nonnull public List<WindowFuture<R, P>> failAll(@Nonnull Throwable t) {
        final List<WindowFuture<R, P>> failed = new ArrayList<>();

        final Iterator<Map.Entry<K, DeferredRequest<K, R, P>>> iterator =
                this.futures.entrySet().iterator();

        while(iterator.hasNext()) {
            final DeferredRequest<K, R, P> future = iterator.next().getValue();
            iterator.remove();

            availableSlots.release();

            future.setException(t);

            failed.add(future);
        }

        return failed;
    }

    @Nullable public WindowFuture<R, P> cancel(@Nonnull K key, boolean mayInterruptIfRunning){

        // try to remove future from window
        final DeferredRequest<K, R, P> future = this.futures.remove(checkNotNull(key));
        if (future == null) {
            return null;
        }

        // signal that a future is completed
        this.availableSlots.release();

        // set failed
        future.cancelInternal(mayInterruptIfRunning);

        return future;
    }


    @Nonnull public List<WindowFuture<R, P>> cancelAll() {
        final List<WindowFuture<R, P>> failed = new ArrayList<>();

        final Iterator<Map.Entry<K, DeferredRequest<K, R, P>>> iterator =
                this.futures.entrySet().iterator();

        while(iterator.hasNext()) {
            final DeferredRequest<K, R, P> future = iterator.next().getValue();
            iterator.remove();

            availableSlots.release();

            future.cancelInternal(true);

            failed.add(future);
        }

        return failed;
    }
}
