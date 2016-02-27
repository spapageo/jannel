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

package com.github.spapageo.jannel.channel;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class ChannelHandlerProviderTest {

    ChannelHandlerProvider channelHandlerProvider = new ChannelHandlerProvider();

    @Test
    public void testCreateMessageLogger() throws Exception {
        assertTrue("Not correct class", channelHandlerProvider.createMessageLogger() instanceof MessageLogger);
    }

    @Test
    public void testCreateMessageEncoder() throws Exception {
        assertTrue("Not correct class", channelHandlerProvider.createMessageEncoder(null) instanceof MessageEncoder);
    }

    @Test
    public void testCreateMessageDecoder() throws Exception {
        assertTrue("Not correct class", channelHandlerProvider.createMessageDecoder(null)
                instanceof MessageDecoder);
    }

    @Test
    public void testCreateMessageLengthDecoder() throws Exception {
        assertTrue("Not correct class", channelHandlerProvider.createMessageLengthDecoder()
                instanceof LengthFieldBasedFrameDecoder);
    }

    @Test
    public void testCreateMessageLengthEncoder() throws Exception {
        assertTrue("Not correct class", channelHandlerProvider.createMessageLengthEncoder()
                instanceof LengthFieldPrepender);
    }

    @Test
    public void testCreateWriteTimeoutHandler() throws Exception {
        assertTrue("Not correct class",
                   channelHandlerProvider.createWriteTimeoutHandler(10, TimeUnit.MILLISECONDS)
                           instanceof WriteTimeoutHandler);
    }

    @Test
    public void testCreateSessionWrapperHandler() throws Exception {
        assertTrue("Not correct class", channelHandlerProvider.createSessionWrapperHandler(null)
                instanceof SessionWrapperHandler);
    }
}