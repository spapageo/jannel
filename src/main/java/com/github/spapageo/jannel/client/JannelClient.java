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

import com.github.spapageo.jannel.channel.ChannelHandlerProvider;
import com.github.spapageo.jannel.channel.Handlers;
import com.github.spapageo.jannel.msg.Admin;
import com.github.spapageo.jannel.msg.AdminCommand;
import com.github.spapageo.jannel.transcode.Transcoder;
import com.github.spapageo.jannel.transcode.TranscoderHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Represents a client to one or more bearer-box sessions
 */
public class JannelClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(JannelClient.class);
    public static final ChannelOption<Long> CONNECT_TIMEOUT_OPTION = ChannelOption.newInstance("connectTimeoutMillis");

    private final EventExecutorGroup sessionExecutor;
    private final ChannelHandlerProvider channelHandlerProvider;
    private final Transcoder transcoder;
    private final Bootstrap clientBootstrap;
    private final EventLoopGroup eventLoopGroup;

    public JannelClient(int ioThreads) {
        this(new NioEventLoopGroup(ioThreads), NioSocketChannel.class);
    }

    public JannelClient(EventLoopGroup eventLoopGroup, Class<? extends Channel> channelClass) {
        this(eventLoopGroup, channelClass, new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()));
    }

    public JannelClient(EventLoopGroup eventLoopGroup,
                        Class<? extends Channel> channelClass,
                        EventExecutorGroup eventExecutors) {
        this(new Bootstrap().group(eventLoopGroup).channel(channelClass),
             eventExecutors,
             new ChannelHandlerProvider(),
             new Transcoder(new TranscoderHelper()));
    }
    
    public JannelClient(Bootstrap clientBootstrap,
                        EventExecutorGroup eventExecutors,
                        ChannelHandlerProvider channelHandlerProvider,
                        Transcoder transcoder) {
        this.eventLoopGroup = clientBootstrap.group();
        this.sessionExecutor = eventExecutors;
        this.channelHandlerProvider = channelHandlerProvider;
        this.transcoder = transcoder;
        this.clientBootstrap = clientBootstrap.handler(new DummyChannelHandler());
    }



    /**
     * Destroys this client and closes all its open sessions
     */
    public void destroy() {
        this.eventLoopGroup.shutdownGracefully().awaitUninterruptibly();
    }

    /**
     * Send an identify admin command to the remote bearer-box
     * @param config the configuration to use for the resulting session
     * @param sessionHandler the session handler to use for the new session
     * @return a newly created session
     */
    public ClientSession identify(ClientSessionConfiguration config, SessionHandler sessionHandler) {
        Channel channel = createConnectedChannel(config.getHost(), config.getPort(), config.getConnectTimeout());

        ClientSession session = createSession(channel, config, sessionHandler);
        session.identify(new Admin(AdminCommand.IDENTIFY, config.getClientId()));
        return session;
    }

    protected ClientSession createSession(Channel channel, ClientSessionConfiguration config, SessionHandler sessionHandler) {
        ClientSession session = new ClientSession(config, channel, sessionHandler);

        ChannelPipeline pipeline = channel.pipeline();

        if(config.getWriteTimeout() > 0)
            pipeline.addLast(Handlers.WRITE_TIMEOUT_HANDLER.name(),
                             channelHandlerProvider.createWriteTimeoutHandler(config.getWriteTimeout(),
                                                                              TimeUnit.MILLISECONDS));


        pipeline.addLast(Handlers.LENGTH_FRAME_DECODER.name(),
                         channelHandlerProvider.createMessageLengthDecoder())
                .addLast(Handlers.LENGTH_FRAME_ENCODER.name(),
                         channelHandlerProvider.createMessageLengthEncoder())
                .addLast(Handlers.MESSAGE_DECODER.name(),
                         channelHandlerProvider.createMessageDecoder(transcoder))
                .addLast(Handlers.MESSAGE_ENCODER.name(),
                         channelHandlerProvider.createMessageEncoder(transcoder))
                .addLast(Handlers.MESSAGE_LOGGER.name(),
                         channelHandlerProvider.createMessageLogger())
                .addLast(sessionExecutor,
                         Handlers.SESSION_WRAPPER.name(),
                         channelHandlerProvider.createSessionWrapperHandler(session))
                //removes the placeholder handler that we added earlier
                .remove(DummyChannelHandler.class);

        return session;
    }

    protected Channel createConnectedChannel(String host, int port, long connectTimeoutMillis) {
        this.clientBootstrap.option(CONNECT_TIMEOUT_OPTION, connectTimeoutMillis);

        ChannelFuture connectFuture = this.clientBootstrap.connect(host, port).syncUninterruptibly();

        LOGGER.info("Successfully connected to bearer-box at: {}", connectFuture.channel().remoteAddress());
        return connectFuture.channel();
    }

    protected static class DummyChannelHandler extends ChannelHandlerAdapter {}

    public EventExecutorGroup getSessionExecutor() {
        return sessionExecutor;
    }

    public ChannelHandlerProvider getChannelHandlerProvider() {
        return channelHandlerProvider;
    }

    public Transcoder getTranscoder() {
        return transcoder;
    }

    public Bootstrap getClientBootstrap() {
        return clientBootstrap;
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }
}
