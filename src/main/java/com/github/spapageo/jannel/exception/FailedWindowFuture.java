/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Spyros Papageorgiou
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

package com.github.spapageo.jannel.exception;

import com.cloudhopper.commons.util.windowing.WindowFuture;

/**
 * Represent a failed future
 * @param <K> the key type
 * @param <R> the request type
 * @param <P> the response type
 */
public class FailedWindowFuture<K,R,P> implements WindowFuture<K, R, P> {

    private final K key;
    private final R request;
    private final Throwable cause;
    private final long timestamp;

    /**
     * Construct a new failed window future
     * @param key the key of this future
     * @param request the request
     * @param cause the cause of the failure
     */
    public FailedWindowFuture(K key, R request, Throwable cause) {
        this.key = key;
        this.request = request;
        this.cause = cause;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public R getRequest() {
        return request;
    }

    @Override
    public P getResponse() {
        return null;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public int getCallerStateHint() {
        return WindowFuture.CALLER_WAITING_TIMEOUT;
    }

    @Override
    public boolean isCallerWaiting() {
        return false;
    }

    @Override
    public int getWindowSize() {
        return 0;
    }

    @Override
    public boolean hasExpireTimestamp() {
        return false;
    }

    @Override
    public long getExpireTimestamp() {
        return 0;
    }

    @Override
    public long getOfferTimestamp() {
        return timestamp;
    }

    @Override
    public long getAcceptTimestamp() {
        return 0;
    }

    @Override
    public long getOfferToAcceptTime() {
        return 0;
    }

    @Override
    public boolean hasDoneTimestamp() {
        return true;
    }

    @Override
    public long getDoneTimestamp() {
        return timestamp;
    }

    @Override
    public long getOfferToDoneTime() {
        return 0;
    }

    @Override
    public long getAcceptToDoneTime() {
        return 0;
    }

    @Override
    public void complete(P response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void complete(P response, long doneTimestamp) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void fail(Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fail(Throwable t, long doneTimestamp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel(long doneTimestamp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean await() throws InterruptedException {
        return true;
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        return true;
    }
}
