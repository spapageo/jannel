package com.github.spapageo.jannel.windowing;

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TimedDeferredRequestTest {

    @Mock
    private Window<Integer, String, Boolean> window;

    @Mock
    private Timer timer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void afterTimeoutFutureIsCancelled() throws Exception {
        when(timer.newTimeout(any(), anyInt(), eq(TimeUnit.MILLISECONDS)))
                .thenAnswer(invocationOnMock -> {invocationOnMock.getArgumentAt(0, TimerTask.class).run(null); return null;});

        DeferredRequest<Integer, String, Boolean> deferredRequest = TimedDeferredRequest.create(5, "request", window, timer, 10000);

        verify(window).fail(eq(5), any(TimeoutException.class));

    }

    @Test
    public void setCancelsTheTimeout() throws Exception {

        Timeout timeout = mock(Timeout.class);

        when(timer.newTimeout(any(), anyInt(), eq(TimeUnit.MILLISECONDS))).thenReturn(timeout);

        DeferredRequest<Integer, String, Boolean> deferredRequest = TimedDeferredRequest.create(5, "request", window, timer, 10000);

        deferredRequest.set(true);

        verify(timeout).cancel();
    }

    @Test
    public void setExceptionCancelsTheTimeout() throws Exception {
        Timeout timeout = mock(Timeout.class);

        when(timer.newTimeout(any(), anyInt(), eq(TimeUnit.MILLISECONDS))).thenReturn(timeout);

        DeferredRequest<Integer, String, Boolean> deferredRequest = TimedDeferredRequest.create(5, "request", window, timer, 10000);

        deferredRequest.setException(new IOException());

        verify(timeout).cancel();
    }

    @Test
    public void cancelInternalCancelsTheTimeout() throws Exception {
        Timeout timeout = mock(Timeout.class);

        when(timer.newTimeout(any(), anyInt(), eq(TimeUnit.MILLISECONDS))).thenReturn(timeout);

        DeferredRequest<Integer, String, Boolean> deferredRequest = TimedDeferredRequest.create(5, "request", window, timer, 10000);

        deferredRequest.cancelInternal(true);

        verify(timeout).cancel();
    }

}