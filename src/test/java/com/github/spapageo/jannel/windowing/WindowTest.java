package com.github.spapageo.jannel.windowing;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class WindowTest {

    private Window<Integer, String, Boolean> window;

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    private Timer timer = new HashedWheelTimer();

    @Before
    public void setUp() throws Exception {
        window = new Window<Integer, String, Boolean>(2, timer);
    }

    @Test
    public void getMaxSize() throws Exception {
        assertEquals(2, window.getMaxSize());
    }

    @Test
    public void getSize() throws Exception {
        assertEquals(0, window.getSize());
        window.offer(1, "request", 1);
        assertEquals(1, window.getSize());
    }

    @Test
    public void containsKey() throws Exception {
        assertFalse(window.containsKey(1));
        window.offer(1, "request", 1);
        assertTrue(window.containsKey(1));
    }

    @Test
    public void get() throws Exception {
        assertNull(window.get(1));
        WindowFuture<String, Boolean> request = window.offer(1, "request", 1);
        WindowFuture<String, Boolean> stringBooleanWindowFuture = window.get(1);
        assertNotNull(stringBooleanWindowFuture);
        assertSame(request, stringBooleanWindowFuture);
        assertEquals("request", stringBooleanWindowFuture.getRequest());
    }

    @Test
    public void offerWhenWindowFullReturnsFailedFuture() throws InterruptedException {
        window.offer(1, "request1", 1);
        window.offer(2, "request2", 1);
        WindowFuture<String, Boolean> request = window.offer(3, "request3", 1);
        assertNotNull(request);

        try {
            request.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof TimeoutException);
        }
    }

    @Test
    public void offerWhenOfferHasDuplicateKeyReturnsFailedFuture() throws InterruptedException {
        window.offer(1, "request1", 1);
        WindowFuture<String, Boolean> request = window.offer(1, "request", 1);
        assertNotNull(request);

        try {
            request.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof DuplicateKeyException);
        }
    }

    @Test
    public void getPendingOfferCount() throws Exception {
        window.offer(1, "request1", 1);
        window.offer(1, "request1", 1);

        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                assertEquals(1, window.getPendingOfferCount());
            }
        }, 1, TimeUnit.MILLISECONDS);

        window.offer(1, "request1", 100);
    }

    @Test
    public void completeReturnsNullForInvalidKey() throws Exception {
        assertNull(window.complete(1, true));
    }

    @Test
    public void completeReturnsCorrectCompletedFutureForCorrectKey() throws Exception {
        final WindowFuture<String, Boolean> request = window.offer(1, "request1", 1);

        final WindowFuture<String, Boolean> completedFuture = window.complete(1, true);

        assertNotNull(completedFuture);
        assertSame(request, completedFuture);
        assertTrue(completedFuture.get());
    }


    @Test
    public void failReturnsNullForInvalidKey() throws Exception {
        assertNull(window.fail(1, new IOException()));
    }

    @Test
    public void failReturnsCorrectFailedFutureForCorrectKey() throws Exception {
        final WindowFuture<String, Boolean> request = window.offer(1, "request1", 1);

        final WindowFuture<String, Boolean> failedFuture = window.fail(1, new IOException());

        assertNotNull(failedFuture);
        assertSame(request, failedFuture);

        try {
            failedFuture.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    @Test
    public void failAllReturnsCorrectNumberOfFailedFutures() throws Exception {
        final WindowFuture<String, Boolean> request = window.offer(1, "request1", 1);
        final WindowFuture<String, Boolean> request2 = window.offer(2, "request1", 1);

        final List<WindowFuture<String, Boolean>> windowFutures = window.failAll(new IOException());

        assertEquals(request, windowFutures.get(0));
        assertEquals(request2, windowFutures.get(1));

        try {
            windowFutures.get(0).get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IOException);
        }

        try {
            windowFutures.get(1).get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    @Test
    public void cancelReturnsNullForInvalidKey() throws Exception {
        assertNull(window.cancel(1, true));
    }

    @Test(expected = CancellationException.class)
    public void cancelReturnsCorrectFailedFutureForCorrectKey() throws Exception {
        final WindowFuture<String, Boolean> request = window.offer(1, "request1", 1);

        final WindowFuture<String, Boolean> canceledFuture = window.cancel(1, true);

        assertNotNull(canceledFuture);
        assertSame(request, canceledFuture);
        assertTrue(request.isCancelled());

        canceledFuture.get();
    }

    @Test
    public void cancelAll() throws Exception {
        final WindowFuture<String, Boolean> request = window.offer(1, "request1", 1);
        final WindowFuture<String, Boolean> request2 = window.offer(2, "request1", 1);

        final List<WindowFuture<String, Boolean>> windowFutures = window.cancelAll();

        assertEquals(request, windowFutures.get(0));
        assertEquals(request2, windowFutures.get(1));

        assertTrue(request.isCancelled());
        assertTrue(request2.isCancelled());
    }

    @Test
    public void destroy() throws Exception {
        window.offer(1, "request1", 1);
        window.offer(2, "request1", 1);

        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                window.destroy();
            }
        }, 10, TimeUnit.MILLISECONDS);
        try {
            window.offer(3, "request1", 1000);
        } catch (InterruptedException e){
            assertEquals(0, window.getPendingOfferCount());
        }
    }

}