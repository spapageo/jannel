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

import com.google.common.util.concurrent.AbstractFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of the {@link WindowFuture} based on the facilities provided by {@link AbstractFuture}
 * @param <K> the key type
 * @param <R> the request type
 * @param <D> the response type
 */
class DeferredRequest<K, R, D> extends AbstractFuture<D> implements WindowFuture<R, D>{

    private final R request;

    private final K key;

    private final Window<K, R, D> window;

    /**
     * Intentional package local constructor
     * @param key the request key
     * @param request the request object
     * @param window the window
     */
    DeferredRequest(@Nonnull K key, @Nonnull R request, @Nonnull Window<K, R, D> window) {
        this.key = checkNotNull(key);
        this.request = checkNotNull(request);
        this.window = checkNotNull(window);
    }

    /**
     * Creates a new {@link DeferredRequest} with the given key and request. This future will be notified on completion,
     * failure or cancellation of the given request.
     * @param key the request key
     * @param request the request object
     * @param window the window object
     * @param <K> the key type
     * @param <R> the request type
     * @param <D> the response type
     * @return the new future object
     */
    @Nonnull
    static <K, R, D> DeferredRequest<K, R, D> create(@Nonnull K key,
                                                     @Nonnull R request,
                                                     @Nonnull Window<K, R, D> window) {
        return new DeferredRequest<>(key, request, window);
    }

    @Override
    @Nonnull
    public R getRequest() {
        return request;
    }

    @Override
    public boolean set(@Nullable D value) {
        return super.set(value);
    }

    @Override
    public boolean setException(@Nonnull Throwable throwable) {
        return super.setException(throwable);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        final WindowFuture<R, D> cancelledFuture = window.cancel(key, mayInterruptIfRunning);

        return cancelledFuture != null && cancelledFuture.isCancelled();
    }

    /**
     * Internal cancellation function for the future
     * @param mayInterruptIfRunning flags whether the task should be cancelled
     * @return true of the task of this future was cancelled, false otherwise.
     */
    boolean cancelInternal(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);
    }
}
