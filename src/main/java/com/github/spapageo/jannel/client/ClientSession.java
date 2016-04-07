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

import com.cloudhopper.commons.util.windowing.*;
import com.github.spapageo.jannel.exception.BadMessageException;
import com.github.spapageo.jannel.exception.FailedWindowFuture;
import com.github.spapageo.jannel.msg.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A client session to a remote bearer-box which can be used to send messages using it member
 * functions and receive message using a registered {@link SessionCallbackHandler}.
 */
public class ClientSession implements SessionCallbackHandler, WindowListener<UUID, Sms, Ack> {

    /**
     * The possible Session states
     */
    private enum State {
        /**
         * Connection open
         */
        OPEN,

        /**
         * Connection open and identified to the remote bearer-box
         */
        IDENTIFIED,

        /**
         * Connection closed
         */
        CLOSED
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSession.class);

    private volatile State state;

    private final ClientSessionConfiguration configuration;

    private final Channel channel;

    private SessionHandler sessionHandler;

    private final Window<UUID, Sms, Ack> sendWindow;

    private final ScheduledExecutorService monitorExecutor;

    /**
     * Construct a new client session to a remote bearer-box
     * @param configuration the client configuration to use for this session
     * @param channel the connected channel to the remote bearer-box
     */
    public ClientSession(ClientSessionConfiguration configuration,
                         Channel channel) {
        this(configuration, channel, null, null);
    }

    /**
     * Construct a new client session to a remote bearer-box
     * @param configuration the client consiguration to use for this session
     * @param channel the connected channel to the remote bearer-box
     * @param sessionHandler the session that will be called every time a specific event occurs
     * @param monitorExecutor the executor to be used for monitoring the request expiration
     */
    public ClientSession(ClientSessionConfiguration configuration,
                         Channel channel,
                         SessionHandler sessionHandler,
                         ScheduledExecutorService monitorExecutor) {
        this.configuration = configuration;
        this.channel = channel;
        this.sessionHandler = sessionHandler == null ? new DefaultSessionHandler() : sessionHandler;
        this.monitorExecutor = monitorExecutor;
        this.state = State.OPEN;

        if (monitorExecutor != null && configuration.getWindowMonitorInterval() > 0) {
            this.sendWindow = new Window<>(configuration.getWindowSize(),
                                           monitorExecutor,
                                           configuration.getWindowMonitorInterval(),
                                           this,
                                           configuration.getClientId());
        } else {
            this.sendWindow = new Window<>(configuration.getWindowSize());
        }
    }

    /**
     * Informs the session that an message was received
     * @param msg the message
     * @throws InterruptedException when the window operation is interrupted
     */
    @Override
    public void fireInboundMessage(Message msg) throws InterruptedException {
        MessageType messageType = msg.getType();

        switch (messageType){
            case HEARTBEAT:
                this.sessionHandler.fireHeartBeatReceived((HeartBeat)msg);
                break;
            case ADMIN:
                this.sessionHandler.fireAdminCommandReceived((Admin)msg);
                break;
            case SMS:
                this.sessionHandler.fireSmsReceived((Sms) msg);
                break;
            case ACK:
                Ack ack = (Ack) msg;
                handleSmsAckResponse(ack, ack.getId());
                break;
            case DATAGRAM:
                LOGGER.warn("Unsupported datagram message received");
                break;
            default:
                LOGGER.warn("Unsupported message received");
                break;
        }
    }

    /**
     * Informs the session that an exception was caught
     * @param throwable the exception
     */
    @Override
    public void fireExceptionCaught(Throwable throwable) {
        if (throwable instanceof BadMessageException) {
            this.sessionHandler.fireBadMessageException((BadMessageException) throwable);
        } else {
            if (isClosed()) {
                LOGGER.debug("Unbind/close was requested, ignoring exception thrown: {}", throwable);
            } else {
                sessionHandler.fireUnknownThrowable(throwable);
            }
        }
    }

    /**
     * Informs the session that the connection was closed
     */
    @Override
    public void fireConnectionClosed() {
        Map<UUID, WindowFuture<UUID, Sms, Ack>> requests = this.sendWindow.createSortedSnapshot();
        Throwable cause = new ClosedChannelException();

        requests.values()
                .stream()
                .filter(WindowFuture::isCallerWaiting)
                .forEach(future -> {
                    LOGGER.debug(
                            "Caller waiting on request [{}], cancelling it with a channel closed exception",
                            future.getKey());
                    future.fail(cause);
                });

        if (isClosed()) {
            LOGGER.debug("Unbind/close was requested, ignoring channelClosed event");
        } else {
            sessionHandler.fireChannelUnexpectedlyClosed();
        }
    }

    /**
     * Synchronously identify to the remote bearer-box
     * @param identifyCommand the identify command
     */
    public void identify(Admin identifyCommand) {
        if(identifyCommand.getAdminCommand() != AdminCommand.IDENTIFY)
            throw new IllegalStateException("The command must be an IDENTIFY");

        try {
            sendMessage(identifyCommand).syncUninterruptibly();
            state = State.IDENTIFIED;
            sessionHandler.fireSessionInitialized(this);
        } catch (Exception throwable){
            LOGGER.error("Exception thrown while trying to identify to the bearer-box.", throwable);
            close();
            throw throwable;
        }
    }

    /**
     * @return true if the session is identified to the remote bearer-box
     */
    public boolean isIdentified() {
        return State.IDENTIFIED.equals(state);
    }

    /**
     * The window callback that inform the session that one of the sms request has expired
     * @param future the window future that expired
     */
    @Override
    public void expired(WindowFuture<UUID, Sms, Ack> future) {
        sessionHandler.fireMessageExpired(future.getRequest());
    }

    /**
     * Closed the session
     */
    public void close() {
        close(5000);
    }

    /**
     * Closes the channel with the specified timeout
     * @param timeoutInMillis the timeout in milliseconds
     */
    public void close(long timeoutInMillis) {
        if (channel.isActive()) {
            channel.close().awaitUninterruptibly(timeoutInMillis);
        }
        this.state = State.CLOSED;
    }

    /**
     * @return whether the session is closed
     */
    public boolean isClosed() {
        return State.CLOSED.equals(state);
    }

    /**
     * Close and destroy this session
     */
    public void destroy() {
        close();
        sendWindow.destroy();
        sessionHandler = null;
    }

    /**
     * Synchronously sends an sms and waits for its response
     * @param sms             the sms to send
     * @param timeoutInMillis the wait timeout until the request if completed
     * @return the response the response
     * @throws InterruptedException   when the operation was interrupted
     */
    public Ack sendSmsAndWait(Sms sms, long timeoutInMillis) throws
                                                             InterruptedException {
        WindowFuture<UUID, Sms, Ack> future = sendSms(sms, timeoutInMillis, true);
        boolean completedWithinTimeout = future.await();

        if (!completedWithinTimeout) {
            future.cancel();
            throw new InterruptedException("Unable to get response for id: " + sms.getId());
        }

        if (future.isCancelled()) {
            throw new InterruptedException("Request was cancelled");
        } else if (!future.isSuccess()){
            Throwable cause = future.getCause();
            throw new ChannelException(cause.getMessage(), cause);
        }

        return future.getResponse();
    }

    /**
     * Asynchronously sends an sms
     * @param sms           the sms to send
     * @param timeoutMillis the timeout for an open window slot to appear
     * @param synchronous   hints whether the caller will be waiting on the future
     * @return the future on the operation
     */
    @SuppressWarnings("unchecked")
    public WindowFuture<UUID, Sms, Ack> sendSms(Sms sms,
                                                long timeoutMillis,
                                                boolean synchronous) {

        // Generate UUID if null
        if (sms.getId() == null) {
            sms.setId(UUID.randomUUID());
        }

        // Apply the current client id if null
        if(sms.getBoxId() == null)
            sms.setBoxId(configuration.getClientId());

        WindowFuture future;
        try {
             future = sendWindow.offer(sms.getId(),
                                       sms,
                                       timeoutMillis,
                                       configuration.getRequestExpiryTimeout(),
                                       synchronous);
        } catch (OfferTimeoutException | DuplicateKeyException | InterruptedException e){
            return new FailedWindowFuture<>(sms.getId(), sms, e);
        }

        sendMessage(sms).addListener(channelFuture -> {
            if (!channelFuture.isSuccess() && !channelFuture.isCancelled()) {
                sendWindow.fail(sms.getId(), channelFuture.cause());
            } else if (channelFuture.isCancelled()) {
                sendWindow.cancel(sms.getId());
            }
        });

        return future;
    }

    /**
     * Send an heartbeat message to the remote server
     * @param heartBeat the heartbeat message
     * @return the channel future of this operation
     */
    public ChannelFuture sendHeartBeat(HeartBeat heartBeat){
        return sendMessage(heartBeat);
    }

    /**
     * Send an ack message to the remote server
     * @param ack the ack message
     * @return the channel future of this operation
     */
    public ChannelFuture sendAck(Ack ack){
        return sendMessage(ack);
    }

    private void handleSmsAckResponse(Ack ack, UUID receivedMsgUUID) throws InterruptedException {
        WindowFuture<UUID, Sms, Ack> future = this.sendWindow.complete(receivedMsgUUID, ack);

        if (future == null) {
            sessionHandler.fireUnexpectedAckReceived(ack);
            return;
        }

        LOGGER.trace("Found a future in the window for id [{}]", ack.getId());

        int callerStateHint = future.getCallerStateHint();
        if (callerStateHint == WindowFuture.CALLER_WAITING) {
            LOGGER.trace("Caller waiting for request: {}", future.getRequest());
            // if a caller is waiting, nothing extra needs done as calling thread will handle the response
        } else if (callerStateHint == WindowFuture.CALLER_NOT_WAITING) {
            LOGGER.trace("Caller not waiting for request: {}", future.getRequest());
            // this was an "expected" response - wrap it into an async response
            sessionHandler.fireExpectedAckReceived(future);
        } else {
            LOGGER.trace("Caller timed out waiting for request: {}",
                         future.getRequest());
            sessionHandler.fireUnexpectedAckReceived(ack);
        }
    }

    private ChannelFuture sendMessage(Message message) {
        return this.channel.writeAndFlush(message);
    }

    /**
     * @return the local address
     */
    public Optional<SocketAddress> getLocalAddress() {
        return Optional.ofNullable(this.channel.localAddress());
    }

    /**
     * @return the remote address
     */
    public  Optional<SocketAddress> getRemoteAddress() {
        return Optional.ofNullable(this.channel.remoteAddress());
    }

    /**
     * @return the session configuration
     */
    public ClientSessionConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * @return the session channel
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * @return the windows of this session
     */
    public Window<UUID, Sms, Ack> getWindow() {
        return this.sendWindow;
    }

    /**
     * @return if the windows monitoring is enabled
     */
    public boolean isWindowMonitorEnabled() {
        return this.monitorExecutor != null && this.configuration.getWindowMonitorInterval() > 0;
    }

    /**
     * @return the maximum window size
     */
    public int getMaxWindowSize() {
        return sendWindow.getMaxSize();
    }

    /**
     * @return the current window size
     */
    public int getWindowSize() {
        return sendWindow.getSize();
    }

    /**
     * @return the handler for this session
     */
    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }
}
