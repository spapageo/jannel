package com.github.spapageo.jannel.windowing;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class InterruptingSemaphoreTest {

    @Test(expected = InterruptedException.class)
    public void tryInterruptInterruptsWaitingThreads() throws Exception {
        final InterruptingSemaphore interruptingSemaphore = new InterruptingSemaphore(0);

        final Timer timer = new HashedWheelTimer();

        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                interruptingSemaphore.tryInterrupt();
            }
        }, 10, TimeUnit.MILLISECONDS);
        interruptingSemaphore.acquire();
    }

}