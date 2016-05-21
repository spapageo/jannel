package com.github.spapageo.jannel.windowing;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class InterruptingSemaphoreTest {

    @Test(expected = InterruptedException.class)
    public void tryInterruptInterruptsWaitingThreads() throws Exception {
        InterruptingSemaphore interruptingSemaphore = new InterruptingSemaphore(0);

        Timer timer = new HashedWheelTimer();

        timer.newTimeout( timeout -> interruptingSemaphore.tryInterrupt(), 10, TimeUnit.MILLISECONDS);
        interruptingSemaphore.acquire();
    }

}