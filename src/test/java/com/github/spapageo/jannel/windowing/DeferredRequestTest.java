package com.github.spapageo.jannel.windowing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeferredRequestTest {

    @Mock
    private Window<Integer, String, Boolean> window;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getRequestReturnsCorrectObject() throws Exception {
        DeferredRequest<Integer, String, Boolean> deferredRequest = DeferredRequest.create(5, "request", window);

        assertEquals("request", deferredRequest.getRequest());
    }

    @Test
    public void setSetsTheFutureValue() throws Exception {

        DeferredRequest<Integer, String, Boolean> deferredRequest = DeferredRequest.create(5, "request", window);
        deferredRequest.set(true);

        assertTrue(deferredRequest.get());
    }

    @Test(expected = IOException.class)
    public void setExceptionMakeTheFutureFail() throws Throwable {
        DeferredRequest<Integer, String, Boolean> deferredRequest = DeferredRequest.create(5, "request", window);
        deferredRequest.setException(new IOException());

        try {
            deferredRequest.get();
        } catch (InterruptedException e) {
            fail();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test
    public void cancelCallTheCancelWindowFunction() throws Exception {
        DeferredRequest<Integer, String, Boolean> deferredRequest = DeferredRequest.create(5, "request", window);

        when(window.cancel(5, true)).thenReturn(deferredRequest);

        deferredRequest.cancel(true);

        verify(window).cancel(5, true);
    }

    @Test
    public void cancelCallTheCancelWindowFunctionAndReturnFalseWhenFutureIsNull() throws Exception {
        DeferredRequest<Integer, String, Boolean> deferredRequest = DeferredRequest.create(5, "request", window);

        when(window.cancel(5, true)).thenReturn(null);

        assertFalse(deferredRequest.cancel(true));

        verify(window).cancel(5, true);
    }

    @Test(expected = CancellationException.class)
    public void cancelInternalCancelsTheFuture() throws Throwable {
        DeferredRequest<Integer, String, Boolean> deferredRequest = DeferredRequest.create(5, "request", window);
        deferredRequest.cancelInternal(true);

        try {
            deferredRequest.get();
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            fail();
        }
    }

}