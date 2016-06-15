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
import com.github.spapageo.jannel.channel.HandlerType;
import com.github.spapageo.jannel.msg.Admin;
import com.github.spapageo.jannel.msg.AdminCommand;
import com.github.spapageo.jannel.transcode.DefaultTranscoder;
import com.github.spapageo.jannel.transcode.Transcoder;
import com.github.spapageo.jannel.transcode.TranscoderHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a client to one or more bearer-box sessions
 */
public class JannelClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(JannelClient.class);
    private static final ChannelOption<Long> CONNECT_TIMEOUT_OPTION = ChannelOption.newInstance("connectTimeoutMillis");

    private final EventExecutorGroup sessionExecutor;
    private final ChannelHandlerProvider channelHandlerProvider;
    private final Transcoder transcoder;
    private final Bootstrap clientBootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final Timer timer;

    public JannelClient(int ioThreads) {
        this(new NioEventLoopGroup(ioThreads), NioSocketChannel.class);
    }

    public JannelClient(EventLoopGroup eventLoopGroup, Class<? extends Channel> channelClass) {
        this(eventLoopGroup, channelClass, new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()));
    }

    public JannelClient(final EventLoopGroup eventLoopGroup,
                        final Class<? extends Channel> channelClass,
                        final EventExecutorGroup eventExecutors) {
        this(new Bootstrap().group(eventLoopGroup).channel(channelClass),
             eventExecutors,
             new ChannelHandlerProvider(),
             new DefaultTranscoder(new TranscoderHelper()),
             new HashedWheelTimer());
    }
    
    public JannelClient(final Bootstrap clientBootstrap,
                        final EventExecutorGroup eventExecutors,
                        final ChannelHandlerProvider channelHandlerProvider,
                        final Transcoder transcoder,
                        final Timer timer) {
        this.eventLoopGroup = clientBootstrap.group();
        this.sessionExecutor = eventExecutors;
        this.channelHandlerProvider = channelHandlerProvider;
        this.transcoder = transcoder;
        this.clientBootstrap = clientBootstrap.handler(new DummyChannelHandler());
        this.timer = timer;
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
    @Nonnull
    public ClientSession identify(ClientSessionConfiguration config,@Nullable SessionHandler sessionHandler) {
        Channel channel = createConnectedChannel(config.getHost(), config.getPort(), config.getConnectTimeout());

        ClientSession session = createSession(channel, config, sessionHandler);
        session.identify(new Admin(AdminCommand.IDENTIFY, config.getClientId()));
        return session;
    }

    protected ClientSession createSession(Channel channel, ClientSessionConfiguration config, @Nullable SessionHandler sessionHandler) {
        ClientSession session = new ClientSession(config, channel, timer, sessionHandler);

        ChannelPipeline pipeline = channel.pipeline();

        if(config.getWriteTimeout() > 0) {
            pipeline.addLast(HandlerType.WRITE_TIMEOUT_HANDLER.name(),
                             channelHandlerProvider.getChangeHandler(HandlerType.WRITE_TIMEOUT_HANDLER,
                                                                     config,
                                                                     session,
                                                                     transcoder));
        }


        pipeline.addLast(HandlerType.LENGTH_FRAME_DECODER.name(),
                         channelHandlerProvider.getChangeHandler(HandlerType.LENGTH_FRAME_DECODER,
                                                                 config,
                                                                 session,
                                                                 transcoder))
                .addLast(HandlerType.LENGTH_FRAME_ENCODER.name(),
                         channelHandlerProvider.getChangeHandler(HandlerType.LENGTH_FRAME_ENCODER,
                                                                 config,
                                                                 session,
                                                                 transcoder))
                .addLast(HandlerType.MESSAGE_DECODER.name(),
                         channelHandlerProvider.getChangeHandler(HandlerType.MESSAGE_DECODER,
                                                                 config,
                                                                 session,
                                                                 transcoder))
                .addLast(HandlerType.MESSAGE_ENCODER.name(),
                         channelHandlerProvider.getChangeHandler(HandlerType.MESSAGE_ENCODER,
                                                                 config,
                                                                 session,
                                                                 transcoder))
                .addLast(HandlerType.MESSAGE_LOGGER.name(),
                         channelHandlerProvider.getChangeHandler(HandlerType.MESSAGE_LOGGER,
                                                                 config,
                                                                 session,
                                                                 transcoder))
                .addLast(sessionExecutor,
                         HandlerType.SESSION_WRAPPER.name(),
                         channelHandlerProvider.getChangeHandler(HandlerType.SESSION_WRAPPER,
                                                                 config,
                                                                 session,
                                                                 transcoder))
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
