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

package com.github.spapageo.jannel.client;

import com.github.spapageo.jannel.exception.BadMessageException;
import com.github.spapageo.jannel.exception.StringSizeException;
import com.github.spapageo.jannel.msg.*;
import com.github.spapageo.jannel.windowing.DuplicateKeyException;
import com.github.spapageo.jannel.windowing.WindowFuture;
import com.google.common.util.concurrent.Futures;
import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ClientSessionTest {

    private ClientSessionConfiguration clientSessionConfiguration;

    private ClientSession clientSession;

    private ScheduledExecutorService scheduledExecutorService;

    private NioEventLoopGroup eventExecutors;

    @Mock(answer = Answers.RETURNS_MOCKS)
    Channel channel;

    @Mock(answer = Answers.RETURNS_MOCKS)
    SessionHandler sessionHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        clientSessionConfiguration = new ClientSessionConfiguration();
        clientSessionConfiguration.setWindowSize(2);
        scheduledExecutorService = new ScheduledThreadPoolExecutor(2);

        clientSession = new ClientSession(clientSessionConfiguration,
                                          channel,
                                          sessionHandler
                                          );
        eventExecutors = new NioEventLoopGroup();
    }

    @Test
    public void testConstruction() throws Exception {
        clientSessionConfiguration.setWindowSize(50);

        ClientSession session = new ClientSession(clientSessionConfiguration,
                                                  channel,
                                                  null);

        SocketAddress remoteSocketAddress = mock(SocketAddress.class);
        SocketAddress localSocketAddress = mock(SocketAddress.class);
        when(channel.remoteAddress()).thenReturn(remoteSocketAddress);
        when(channel.localAddress()).thenReturn(localSocketAddress);

        assertSame("Incorrect configuration", clientSessionConfiguration, session.getConfiguration());
        assertSame("Incorrect channel", channel, session.getChannel());
        assertEquals("Incorrect window size",
                     clientSessionConfiguration.getWindowSize(),
                     session.getMaxWindowSize());
        assertNotNull("Null window", session.getWindow());
        assertTrue("Not the default handler", session.getSessionHandler() instanceof DefaultSessionHandler);
        assertSame("Not the correct remote address", remoteSocketAddress, session.getRemoteAddress().get());
        assertSame("Not the correct local address", localSocketAddress, session.getLocalAddress().get());
        assertEquals("Default window size must be 0", 0, clientSession.getWindowSize());

    }

    @Test
    public void testConstructionWithCustomSessionHandler() throws Exception {

        SessionHandler sessionHandler = mock(SessionHandler.class);

        ClientSession session = new ClientSession(clientSessionConfiguration,
                                                  channel,
                                                  sessionHandler);

        assertSame("Wrong session handler", sessionHandler, session.getSessionHandler());
    }

    @Test
    public void testSendAck() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();

        when(channel.writeAndFlush(any())).thenReturn(promise);

        Ack ack = mock(Ack.class);
        assertSame(promise, clientSession.sendAck(ack));

        verify(channel).writeAndFlush(ack);
    }

    @Test
    public void testIdentifyWhenCommandIsIdentifyWritesToChannel() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();


        when(channel.writeAndFlush(any())).thenReturn(promise);

        Admin admin = new Admin();
        admin.setBoxId("test");
        admin.setAdminCommand(AdminCommand.IDENTIFY);

        clientSession.identify(admin);

        verify(channel).writeAndFlush(admin);
        verify(sessionHandler).fireSessionInitialized(clientSession);

        assertTrue(clientSession.isIdentified());
    }



    @Test(expected = IllegalStateException.class)
    public void testIdentifyWhenCommandIsNotIdentifyThrows() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();

        when(channel.writeAndFlush(any())).thenReturn(promise);

        Admin admin = new Admin();
        admin.setBoxId("test");
        admin.setAdminCommand(AdminCommand.RESTART);

        clientSession.identify(admin);
    }

    @Test(expected = IOException.class)
    public void testIdentifyWhenWriteFailsAndChannelIsActiveClosesChannelAndThrows() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setFailure(new IOException("test"));

        when(channel.writeAndFlush(any())).thenReturn(promise);
        when(channel.isActive()).thenReturn(true);
        when(channel.close()).thenReturn(promise);

        Admin admin = new Admin();
        admin.setBoxId("test");
        admin.setAdminCommand(AdminCommand.IDENTIFY);

        try {
            clientSession.identify(admin);
        } finally {
            verify(channel).writeAndFlush(admin);
            verify(channel).close();

            assertTrue(clientSession.isClosed());
        }
    }

    @Test(expected = IOException.class)
    public void testIdentifyWhenWriteFailsAndChannelIsInactiveSetsClosedState() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setFailure(new IOException("test"));

        when(channel.writeAndFlush(any())).thenReturn(promise);
        when(channel.isActive()).thenReturn(false);

        Admin admin = new Admin();
        admin.setBoxId("test");
        admin.setAdminCommand(AdminCommand.IDENTIFY);

        try {
            clientSession.identify(admin);
        } finally {
            verify(channel).writeAndFlush(admin);
            verify(channel, times(0)).close();

            assertTrue(clientSession.isClosed());
        }
    }

    @Test
    public void testCloseWhenChannelIsActiveClosesChannel() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();

        when(channel.isActive()).thenReturn(true);
        when(channel.close()).thenReturn(promise);

        clientSession.close();

        verify(channel).close();
    }

    @Test
    public void testCloseWhenChannelIsInactiveClosesChannel() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();

        when(channel.isActive()).thenReturn(false);
        when(channel.close()).thenReturn(promise);

        clientSession.close();

        verify(channel, times(0)).close();
    }

    @Test
    public void testDestroysClosesChannelAndDestroysTheWindow() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();

        when(channel.isActive()).thenReturn(false);
        when(channel.close()).thenReturn(promise);

        WindowFuture windowFuture = clientSession.getWindow().offer(UUID.randomUUID(), new Sms(), 5000);
        clientSession.destroy();

        verify(channel, times(0)).close();
        assertNull("Session handler must be null after destruction", clientSession.getSessionHandler());
        assertTrue("The outstanding requests should be canceled", windowFuture.isCancelled());

    }

    @Test
    public void testSendSmsSetsUUIDAndBoxIdWhenNull() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();

        when(channel.writeAndFlush(any())).thenReturn(promise);

        Sms sms = new Sms();

        clientSession.sendSms(sms, 5000);

        assertNotNull(sms.getId());
        assertSame(clientSessionConfiguration.getClientId(), sms.getBoxId());
        verify(channel).writeAndFlush(sms);
    }

    @Test(expected = DuplicateKeyException.class)
    public void testSendSmsReturnsFailedFutureWhenOfferToWindowFails() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();

        when(channel.writeAndFlush(any())).thenReturn(promise);

        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());
        sms.setBoxId("test box");

        //add the sms so the next offer fails
        clientSession.getWindow().offer(sms.getId(), sms, 5000);

        WindowFuture<Sms, Ack> future = clientSession.sendSms(sms, 5000);
        assertFalse(future.isCancelled());

        assertSame(sms, future.getRequest());

        Futures.getChecked(future, DuplicateKeyException.class);
    }

    @Test(expected = IOException.class)
    public void testSendSmsReturnsFailedFutureWhenWriteFails() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setFailure(new IOException());

        when(channel.writeAndFlush(any())).thenReturn(promise);

        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());
        sms.setBoxId("test box");

        WindowFuture<Sms, Ack> future = clientSession.sendSms(sms, 5000);

        Futures.getChecked(future, IOException.class);
    }

    @Test(expected = CancellationException.class)
    public void testSendSmsReturnsFailedFutureWhenWriteIsCancelled() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.cancel(true);

        when(channel.writeAndFlush(any())).thenReturn(promise);

        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());
        sms.setBoxId("test box");

        WindowFuture<Sms, Ack> future = clientSession.sendSms(sms, 5000);

        Futures.getUnchecked(future);
    }

    @Test
    public void testFireInboundMessageWhenIsAckAndCompletedRequestDoesNotNotifyHandler()
            throws Exception {
        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());
        Ack message = new Ack(sms.getId());

        WindowFuture<Sms, Ack> future = clientSession.sendSms(sms, 10000);
        clientSession.fireInboundMessage(message);

        final Ack ack = future.get();

        assertSame(message, ack);
        verifyNoMoreInteractions(sessionHandler);
    }

    @Test(expected = InterruptedByTimeoutException.class)
    public void testFireInboundMessageWhenIsAckAndTimedOutRequestCallsUnexpectedAck()
            throws Exception {
        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());
        Ack message = new Ack(sms.getId());

        clientSessionConfiguration.setRequestExpiryTimeout(1);
        final WindowFuture<Sms, Ack> future = clientSession.sendSms(sms, 1);

        try {
            Futures.getChecked(future, InterruptedByTimeoutException.class);
        } catch (InterruptedByTimeoutException e) {
            clientSession.fireInboundMessage(message);

            verify(sessionHandler).fireUnexpectedAckReceived(message);
            throw e;
        }
    }

    @Test
    public void testFireInboundMessageWhenIsAckAndInvalidIdRequestCallsUnexpectedAck()
            throws Exception {
        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());
        Ack message = new Ack(UUID.randomUUID());

        clientSession.sendSms(sms, 100000);

        clientSession.fireInboundMessage(message);

        verify(sessionHandler).fireUnexpectedAckReceived(message);
    }

    @Test
    public void testSendHeartBeat() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();

        when(channel.writeAndFlush(any())).thenReturn(promise);

        HeartBeat heartBeat = new HeartBeat();

        assertSame(promise, clientSession.sendHeartBeat(heartBeat));
        verify(channel).writeAndFlush(heartBeat);
    }

    @Test
    public void testFireInboundMessageWhenIsHeartBeatCallsHeadBeatReceived() throws Exception {
        HeartBeat heartBeat = new HeartBeat();

        clientSession.fireInboundMessage(heartBeat);

        verify(sessionHandler).fireHeartBeatReceived(heartBeat);
    }


    @Test
    public void testFireInboundMessageWhenIsAdminCallsAdminReceived() throws Exception {
        Admin admin = new Admin();

        clientSession.fireInboundMessage(admin);

        verify(sessionHandler).fireAdminCommandReceived(admin);
    }

    @Test
    public void testFireInboundMessageWhenIsSmsCallsSmsReceived() throws Exception {
        Sms sms = new Sms();

        clientSession.fireInboundMessage(sms);

        verify(sessionHandler).fireSmsReceived(sms);
    }

    @Test
    public void testFireInboundMessageWhenIsDatagramItIsIgnored() throws Exception {
        Datagram datagram = new Datagram();

        clientSession.fireInboundMessage(datagram);

        verifyNoMoreInteractions(sessionHandler, channel);
    }

    @Test
    public void testFireInboundMessageWhenIsUnknownItIsIgnored() throws Exception {
        Message message = () -> MessageType.UNKNOWN;

        clientSession.fireInboundMessage(message);

        verifyNoMoreInteractions(sessionHandler, channel);
    }

    @Test
    public void testFireExceptionCaughtThenBadMessageExceptionCallsFireBadMessage()
            throws Exception {

        BadMessageException exception = new StringSizeException("test");

        clientSession.fireExceptionCaught(exception);

        verify(sessionHandler).fireBadMessageException(exception);
    }

    @Test
    public void testFireExceptionCaughtWhenBadMessageExceptionCallsFireUnknownThrowable()
            throws Exception {

        IllegalArgumentException illegalArgumentException = new IllegalArgumentException();

        clientSession.fireExceptionCaught(illegalArgumentException);

        verify(sessionHandler).fireUnknownThrowable(illegalArgumentException);
    }

    @Test
    public void testFireExceptionCaughtWhenBadMessageExceptionAndSessionClosedDoesNothing()
            throws Exception {

        IllegalArgumentException illegalArgumentException = new IllegalArgumentException();

        clientSession.close();
        clientSession.fireExceptionCaught(illegalArgumentException);

        verifyNoMoreInteractions(sessionHandler);
    }

    @Test(expected = ClosedChannelException.class)
    public void testConnectionClosedFailsOutStandingConnectionAndCallUnexpectedlyClosed()
            throws Exception {
        Sms sms = new Sms();

        WindowFuture<Sms, Ack> future = clientSession.sendSms(sms, 10000);
        clientSession.fireConnectionClosed();

        try {
            Futures.getChecked(future, ClosedChannelException.class);
        } finally {
         verify(sessionHandler).fireChannelUnexpectedlyClosed();
        }
    }

    @Test
    public void testConnectionClosedWhenChannelIsClosedDoesNotInvokeHandler() throws Exception {
        clientSession.close();
        clientSession.fireConnectionClosed();

        verifyNoMoreInteractions(sessionHandler);
    }

    @Test
    public void testSendSmsAndWaitReturnsCorrectResponse() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();

        when(channel.writeAndFlush(any())).thenReturn(promise);

        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());
        sms.setBoxId("test box");

        Ack expectedResponse = new Ack();

        scheduledExecutorService.schedule(() -> {
                clientSession.getWindow().complete(sms.getId(), expectedResponse);
        }, 100, TimeUnit.MILLISECONDS);

        Ack response = clientSession.sendSmsAndWait(sms, 5000);
        assertSame(expectedResponse, response);
    }

    @Test
    public void testSendSmsAndWaitThrowsWhenOfferToWindowTimesOut() {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();

        when(channel.writeAndFlush(any())).thenReturn(promise);

        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());
        sms.setBoxId("test box");
        clientSessionConfiguration.setRequestExpiryTimeout(1);

        try {
            clientSession.sendSmsAndWait(sms, 1);
        } catch (InterruptedException e) {
            fail("Not the correct exception");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof TimeoutException);
        }
    }

    @Test
    public void testSendSmsAndWaitThrowsWhenOfferToWindowFails() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setSuccess();

        when(channel.writeAndFlush(any())).thenReturn(promise);

        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());
        sms.setBoxId("test box");

        //add the sms so the next offer fails
        clientSession.getWindow().offer(sms.getId(), sms, 5000);

        try {
            clientSession.sendSmsAndWait(sms, 5000);
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof DuplicateKeyException);
        }
    }

    @Test
    public void testSendSmsAndWaitThrowsWhenWriteFails() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.setFailure(new IOException());

        when(channel.writeAndFlush(any())).thenReturn(promise);

        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());
        sms.setBoxId("test box");

        try {
            clientSession.sendSmsAndWait(sms, 5000);
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    @Test(expected = CancellationException.class)
    public void testSendSmsAndWaitThrowsWhenWriteIsCancelled() throws Exception {
        DefaultChannelPromise promise = new DefaultChannelPromise(channel, eventExecutors.next());
        promise.cancel(true);

        when(channel.writeAndFlush(any())).thenReturn(promise);

        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());
        sms.setBoxId("test box");

        clientSession.sendSmsAndWait(sms, 5000);
    }
}