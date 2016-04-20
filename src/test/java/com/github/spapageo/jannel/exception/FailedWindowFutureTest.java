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
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class FailedWindowFutureTest {

    FailedWindowFuture<String, String, String> future;

    @Before
    public void before(){
        future = new FailedWindowFuture<>("id", "request", new IOException());
    }

    @Test
    public void testGetKey() throws Exception {
        assertEquals("id", future.getKey());
    }

    @Test
    public void testGetRequest() throws Exception {
        assertEquals("request", future.getRequest());
    }

    @Test
    public void testGetResponse() throws Exception {
        assertNull(future.getResponse());
    }

    @Test
    public void testIsDone() throws Exception {
        assertTrue(future.isDone());
    }

    @Test
    public void testIsSuccess() throws Exception {
        assertFalse(future.isSuccess());
    }

    @Test
    public void testGetCause() throws Exception {
        assertTrue(future.getCause() instanceof IOException);
    }

    @Test
    public void testIsCancelled() throws Exception {
        assertFalse(future.isCancelled());
    }

    @Test
    public void testGetCallerStateHint() throws Exception {
        assertEquals(WindowFuture.CALLER_WAITING_TIMEOUT, future.getCallerStateHint());
    }

    @Test
    public void testIsCallerWaiting() throws Exception {
        assertFalse(future.isCallerWaiting());
    }

    @Test
    public void testGetWindowSize() throws Exception {
        assertEquals(0, future.getWindowSize());
    }

    @Test
    public void testHasExpireTimestamp() throws Exception {
        assertFalse(future.hasExpireTimestamp());
    }

    @Test
    public void testHasDoneTimestamp() throws Exception {
        assertTrue(future.hasDoneTimestamp());
    }

    @Test
    public void testGetExpireTimestamp() throws Exception {
        assertEquals(0, future.getExpireTimestamp());

    }

    @Test
    public void testGetOfferTimestamp() throws Exception {
        assertTrue(future.getOfferTimestamp() <= System.currentTimeMillis());
    }

    @Test
    public void testGetAcceptTimestamp() throws Exception {
        assertEquals(0, future.getAcceptTimestamp());
    }

    @Test
    public void testGetOfferToAcceptTime() throws Exception {
        assertEquals(0, future.getOfferToAcceptTime());
    }

    @Test
    public void testGetDoneTimestamp() throws Exception {
        assertTrue(future.getDoneTimestamp() <= System.currentTimeMillis());
    }

    @Test
    public void testGetOfferToDoneTime() throws Exception {
        assertEquals(0, future.getOfferToDoneTime());
    }

    @Test
    public void testGetAcceptToDoneTime() throws Exception {
        assertEquals(0, future.getAcceptToDoneTime());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testComplete() throws Exception {
        future.complete("test");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testComplete1() throws Exception {
        future.complete("test", 1L);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFail() throws Exception {
        future.fail(new IOException());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFail1() throws Exception {
        future.fail(new IOException(), 5L);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCancel() throws Exception {
        future.cancel();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCancel1() throws Exception {
        future.cancel(5L);
    }

    @Test
    public void testAwait() throws Exception {
        assertTrue(future.await());
    }

    @Test
    public void testAwait1() throws Exception {
        assertTrue(future.await(5L));
    }
}