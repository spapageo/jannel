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

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * A {@link DeferredRequest} that possesses a timeout
 * @param <K> the key type
 * @param <R> the request type
 * @param <D> the done type
 */
final class TimedDeferredRequest<K, R, D> extends DeferredRequest<K, R, D> {
    private final Timeout timeout;

    /**
     * Intentional private local constructor
     * @param key the request key
     * @param request the request object
     * @param window the window
     * @param timeoutMillis the time after which this future will be cancelled
     * @param timer the timer used to implement the timeout functionality
     */
    private TimedDeferredRequest(final K key,
                                 final R request,
                                 final Window<K, R, D> window,
                                 final Timer timer,
                                 final long timeoutMillis) {
        super(key, request, window);
        this.timeout = checkNotNull(timer).newTimeout(new TimerTask() {
                                                          @Override
                                                          public void run(Timeout timerTask) throws Exception {
                                                              window.fail(checkNotNull(key),
                                                                          new TimeoutException("The operation timed out (Window full)"));
                                                          }
                                                      },
                                                      timeoutMillis,
                                                      TimeUnit.MILLISECONDS);
    }

    /**
     * Creates a new {@link DeferredRequest} with the given key, request that also has an expiration timeout.
     * This future will be notified on completion, failure or cancellation of the given request. In case of cancellation
     * the failure cause will be TimeoutException.
     * @param key the request key
     * @param request the request object
     * @param window the window
     * @param timeoutMillis the time after which this future will be cancelled
     * @param timer the timer used to implement the timeout functionality
     * @param <K> the key type
     * @param <R> the request type
     * @param <D> the done type
     */
    @Nonnull
    static <K, R, D> TimedDeferredRequest<K, R, D> create(final K key,
                                                          final R request,
                                                          final Window<K, R, D> window,
                                                          final Timer timer,
                                                          final long timeoutMillis) {
        return new TimedDeferredRequest<K, R, D>(key, request, window, timer, timeoutMillis);
    }

    @Override
    public boolean set(@Nullable D value) {
        timeout.cancel();
        return super.set(value);
    }

    @Override
    public boolean setException(Throwable throwable) {
        timeout.cancel();
        return super.setException(throwable);
    }

    @Override
    public boolean cancelInternal(boolean mayInterruptIfRunning) {
        timeout.cancel();
        return super.cancelInternal(mayInterruptIfRunning);
    }
}
