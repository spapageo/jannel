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
import com.github.spapageo.jannel.transcode.Transcoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.util.Timer;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.SucceededFuture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

public class JannelClientTest {

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private EventLoopGroup eventLoopGroup;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private Bootstrap bootstrap;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private EventExecutorGroup eventExecutors;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private Transcoder transcoder;

    @Mock(answer = Answers.RETURNS_MOCKS)
    private ChannelHandlerProvider channelHandlerProvider;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private ScheduledExecutorService scheduledExecutorService;

    private JannelClient jannelClient;
    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private ChannelHandler mockLengthWriteHandler;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private ChannelHandler mockLengthReadHandler;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private ChannelHandler mockDecoderHandler;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private ChannelHandler mockEncoderHandler;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private ChannelHandler mockLoggerHandler;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private ChannelHandler mockSessionHandler;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private ChannelHandler mockWriteHandler;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private Timer mockTimer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(bootstrap.group()).thenReturn(eventLoopGroup);
        when(bootstrap.handler(any(ChannelHandler.class))).thenReturn(bootstrap);

        jannelClient = new JannelClient(bootstrap,
                                        eventExecutors,
                                        channelHandlerProvider,
                                        transcoder,
                                        mockTimer);
    }

    @Test
    public void testConstruction() throws Exception {
        JannelClient jannelClient = new JannelClient(4);

        assertTrue("Wrong default event loop class", jannelClient.getEventLoopGroup() instanceof NioEventLoopGroup);
        assertTrue("Null channel handler", jannelClient.getChannelHandlerProvider() != null);
        assertTrue("Null transcoder", jannelClient.getTranscoder() != null);
        assertTrue("Null bootstrap", jannelClient.getClientBootstrap() != null);
        assertTrue("Wrong default event executor class", jannelClient.getSessionExecutor() instanceof NioEventLoopGroup);
    }

    @Test
    public void testDefaultHandlerClassIsCorrect() throws Exception {
        verify(bootstrap).handler(any(JannelClient.DummyChannelHandler.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() throws Exception {
        Future<String> future = new SucceededFuture<String>(ImmediateEventExecutor.INSTANCE, "test");
        when(eventLoopGroup.shutdownGracefully()).thenReturn((Future) future);
        jannelClient.destroy();

        verify(eventLoopGroup).shutdownGracefully();
    }

    @Test
    public void testIdentifyAddsTheCorrectlyHandlersToThePipelineWithEnabledWriteTimeout() throws Exception {
        Channel channel = mock(Channel.class, Answers.RETURNS_SMART_NULLS.get());
        mockWriteHandler = mock(ChannelHandler.class);

        DefaultChannelPromise completedFuture = new DefaultChannelPromise(channel);
        completedFuture.setSuccess();

        ChannelPipeline channelPipeline = mock(ChannelPipeline.class);
        when(channelPipeline.addLast(anyString(), any(ChannelHandler.class))).thenReturn(channelPipeline);
        when(channelPipeline.addLast(any(EventExecutorGroup.class), anyString(), any(ChannelHandler.class))).thenReturn(channelPipeline);
        when(channel.pipeline()).thenReturn(channelPipeline);
        when(channel.isActive()).thenReturn(true);
        when(channel.writeAndFlush(any())).thenReturn(completedFuture);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.WRITE_TIMEOUT_HANDLER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockWriteHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.LENGTH_FRAME_ENCODER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockLengthWriteHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.LENGTH_FRAME_DECODER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockLengthReadHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.MESSAGE_ENCODER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockEncoderHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.MESSAGE_DECODER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockDecoderHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.MESSAGE_LOGGER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockLoggerHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.SESSION_WRAPPER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockSessionHandler);

        when(bootstrap.connect(anyString(), anyInt())).thenReturn(completedFuture);

        InOrder pipelineOrder = inOrder(channelPipeline);

        ClientSessionConfiguration configuration = new ClientSessionConfiguration();
        configuration.setWriteTimeout(1000);

        jannelClient.identify(configuration, null);

        pipelineOrder.verify(channelPipeline).addLast(HandlerType.WRITE_TIMEOUT_HANDLER.name(),
                                                      mockWriteHandler);
        pipelineOrder.verify(channelPipeline).addLast(HandlerType.LENGTH_FRAME_DECODER.name(), mockLengthReadHandler);
        pipelineOrder.verify(channelPipeline).addLast(HandlerType.LENGTH_FRAME_ENCODER.name(), mockLengthWriteHandler);
        pipelineOrder.verify(channelPipeline).addLast(HandlerType.MESSAGE_DECODER.name(), mockDecoderHandler);
        pipelineOrder.verify(channelPipeline).addLast(HandlerType.MESSAGE_ENCODER.name(), mockEncoderHandler);
        pipelineOrder.verify(channelPipeline).addLast(HandlerType.MESSAGE_LOGGER.name(), mockLoggerHandler);
        pipelineOrder.verify(channelPipeline).addLast(eventExecutors, HandlerType.SESSION_WRAPPER.name(), mockSessionHandler);
        pipelineOrder.verify(channelPipeline).remove(JannelClient.DummyChannelHandler.class);

        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.WRITE_TIMEOUT_HANDLER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.LENGTH_FRAME_DECODER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.LENGTH_FRAME_ENCODER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.MESSAGE_DECODER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.MESSAGE_ENCODER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.MESSAGE_LOGGER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.SESSION_WRAPPER), eq(configuration), any(ClientSession.class), eq(transcoder));
    }

    @Test
    public void testIdentifyAddsTheCorrectlyHandlersToThePipelineWithDisabledWriteTimeout() throws Exception {
        Channel channel = mock(Channel.class, Answers.RETURNS_SMART_NULLS.get());

        DefaultChannelPromise completedFuture = new DefaultChannelPromise(channel);
        completedFuture.setSuccess();

        ChannelPipeline channelPipeline = mock(ChannelPipeline.class);
        when(channelPipeline.addLast(anyString(), any(ChannelHandler.class))).thenReturn(channelPipeline);
        when(channelPipeline.addLast(any(EventExecutorGroup.class), anyString(), any(ChannelHandler.class))).thenReturn(channelPipeline);
        when(channel.pipeline()).thenReturn(channelPipeline);
        when(channel.isActive()).thenReturn(true);
        when(channel.writeAndFlush(any())).thenReturn(completedFuture);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.WRITE_TIMEOUT_HANDLER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockWriteHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.LENGTH_FRAME_ENCODER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockLengthWriteHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.LENGTH_FRAME_DECODER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockLengthReadHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.MESSAGE_ENCODER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockEncoderHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.MESSAGE_DECODER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockDecoderHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.MESSAGE_LOGGER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockLoggerHandler);
        when(channelHandlerProvider.getChangeHandler(eq(HandlerType.SESSION_WRAPPER),
                                                     any(ClientSessionConfiguration.class),
                                                     any(ClientSession.class),
                                                     any(Transcoder.class))).thenReturn(mockSessionHandler);

        when(bootstrap.connect(anyString(), anyInt())).thenReturn(completedFuture);

        InOrder pipelineOrder = inOrder(channelPipeline);

        ClientSessionConfiguration configuration = new ClientSessionConfiguration();

        jannelClient.identify(configuration, null);

        pipelineOrder.verify(channelPipeline).addLast(HandlerType.LENGTH_FRAME_DECODER.name(),
                                                      mockLengthReadHandler);
        pipelineOrder.verify(channelPipeline).addLast(HandlerType.LENGTH_FRAME_ENCODER.name(),
                                                      mockLengthWriteHandler);
        pipelineOrder.verify(channelPipeline).addLast(HandlerType.MESSAGE_DECODER.name(),
                                                      mockDecoderHandler);
        pipelineOrder.verify(channelPipeline).addLast(HandlerType.MESSAGE_ENCODER.name(),
                                                      mockEncoderHandler);
        pipelineOrder.verify(channelPipeline).addLast(HandlerType.MESSAGE_LOGGER.name(),
                                                      mockLoggerHandler);
        pipelineOrder.verify(channelPipeline).addLast(eventExecutors, HandlerType.SESSION_WRAPPER.name(),
                                                      mockSessionHandler);
        pipelineOrder.verify(channelPipeline).remove(JannelClient.DummyChannelHandler.class);

        verify(channelHandlerProvider, times(0)).getChangeHandler(eq(HandlerType.WRITE_TIMEOUT_HANDLER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.LENGTH_FRAME_DECODER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.LENGTH_FRAME_ENCODER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.MESSAGE_DECODER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.MESSAGE_ENCODER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.MESSAGE_LOGGER), eq(configuration), any(ClientSession.class), eq(transcoder));
        verify(channelHandlerProvider).getChangeHandler(eq(HandlerType.SESSION_WRAPPER), eq(configuration), any(ClientSession.class), eq(transcoder));
    }


    @Test
    public void testIdentifyConnectsToCorrectRemoteServerWithConnectionTimeout() throws Exception {

        Channel channel = mock(Channel.class, Answers.RETURNS_SMART_NULLS.get());
        mockWriteHandler = mock(ChannelHandler.class);

        DefaultChannelPromise completedFuture = new DefaultChannelPromise(channel);
        completedFuture.setSuccess();

        ChannelPipeline channelPipeline = mock(ChannelPipeline.class);
        when(channelPipeline.addLast(anyString(), any(ChannelHandler.class))).thenReturn(channelPipeline);
        when(channelPipeline.addLast(any(EventExecutorGroup.class), anyString(), any(ChannelHandler.class))).thenReturn(channelPipeline);
        when(channel.pipeline()).thenReturn(channelPipeline);
        when(channel.isActive()).thenReturn(true);
        when(channel.writeAndFlush(any())).thenReturn(completedFuture);

        when(bootstrap.connect(anyString(), anyInt())).thenReturn(completedFuture);

        ClientSessionConfiguration configuration = new ClientSessionConfiguration();
        configuration.setHost("testHost");
        configuration.setPort(1111);
        configuration.setConnectTimeout(10000);

        jannelClient.identify(configuration, null);

        verify(bootstrap).connect(configuration.getHost(), configuration.getPort());
        verify(bootstrap).option(ChannelOption.valueOf("connectTimeoutMillis"), configuration.getConnectTimeout());
    }

    @Test
    public void testIdentifySendsCorrectIdentifyCommand() throws Exception {
        Channel channel = mock(Channel.class, Answers.RETURNS_SMART_NULLS.get());
        mockWriteHandler = mock(ChannelHandler.class);

        DefaultChannelPromise completedFuture = new DefaultChannelPromise(channel);
        completedFuture.setSuccess();

        ChannelPipeline channelPipeline = mock(ChannelPipeline.class);
        when(channelPipeline.addLast(anyString(), any(ChannelHandler.class))).thenReturn(channelPipeline);
        when(channelPipeline.addLast(any(EventExecutorGroup.class), anyString(), any(ChannelHandler.class))).thenReturn(channelPipeline);
        when(channel.pipeline()).thenReturn(channelPipeline);
        when(channel.isActive()).thenReturn(true);
        when(channel.writeAndFlush(any())).thenReturn(completedFuture);

        when(bootstrap.connect(anyString(), anyInt())).thenReturn(completedFuture);

        ClientSessionConfiguration configuration = new ClientSessionConfiguration();
        configuration.setClientId("testId");

        jannelClient.identify(configuration, null);

        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);

        verify(channel).writeAndFlush(captor.capture());

        Admin command = captor.getValue();

        assertEquals("Wrong command type", AdminCommand.IDENTIFY, command.getAdminCommand());
        assertEquals("Wrong client id", configuration.getClientId(), command.getBoxId());
    }

    @Test(expected = PrematureChannelClosureException.class)
    public void testIdentifyCloseChannelOnFailure() throws Exception {
        Channel channel = mock(Channel.class, Answers.RETURNS_SMART_NULLS.get());
        mockWriteHandler = mock(ChannelHandler.class);

        DefaultChannelPromise completedFuture = new DefaultChannelPromise(channel);
        completedFuture.setSuccess();

        DefaultChannelPromise failedFuture = new DefaultChannelPromise(channel);
        failedFuture.setFailure(new PrematureChannelClosureException("test"));

        ChannelPipeline channelPipeline = mock(ChannelPipeline.class);
        when(channelPipeline.addLast(anyString(), any(ChannelHandler.class))).thenReturn(channelPipeline);
        when(channelPipeline.addLast(any(EventExecutorGroup.class), anyString(), any(ChannelHandler.class))).thenReturn(channelPipeline);
        when(channel.pipeline()).thenReturn(channelPipeline);
        when(channel.isActive()).thenReturn(true);
        when(channel.writeAndFlush(any())).thenReturn(failedFuture);
        when(channel.close()).thenReturn(completedFuture);

        when(bootstrap.connect(anyString(), anyInt())).thenReturn(completedFuture);

        ClientSessionConfiguration configuration = new ClientSessionConfiguration();

        jannelClient.identify(configuration, null);
    }
}