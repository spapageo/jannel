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

import com.github.spapageo.jannel.client.SessionCallbackHandler;
import com.github.spapageo.jannel.transcode.Transcoder;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 * Channel handler factory
 */
public class ChannelHandlerProvider {

    /**
     * The maximum message size
     */
    public static final int MAXIMUM_MESSAGE_BYTE_SIZE = 64 * 1024;

    /**
     * The offset of the message field in the binary representation of a message
     */
    public static final int MESSAGE_FIELD_OFFSET = 0;

    /**
     * The size of the length field in bytes
     */
    public static final int LENGTH_FIELD_SIZE = 4;

    /**
     * Shared logger handler instance
     */
    private static final ChannelHandler MESSAGE_LOGGER = new MessageLogger();

    public ChannelHandler createMessageLogger(){
        return MESSAGE_LOGGER;
    }

    public ChannelHandler createMessageEncoder(Transcoder transcoder){
        return new MessageEncoder(transcoder);
    }

    public ChannelHandler createMessageDecoder(Transcoder transcoder){
        return new MessageDecoder(transcoder);
    }

    public ChannelHandler createMessageLengthDecoder(){
        return new LengthFieldBasedFrameDecoder(MAXIMUM_MESSAGE_BYTE_SIZE,
                                                MESSAGE_FIELD_OFFSET,
                                                LENGTH_FIELD_SIZE,
                                                0,
                                                LENGTH_FIELD_SIZE);
    }

    public ChannelHandler createMessageLengthEncoder(){
        return new LengthFieldPrepender(LENGTH_FIELD_SIZE, false);
    }

    public ChannelHandler createWriteTimeoutHandler(long timeout, TimeUnit unit){
        return new WriteTimeoutHandler(timeout, unit);
    }

    public ChannelHandler createSessionWrapperHandler(SessionCallbackHandler sessionCallbackHandler){
        return new SessionWrapperHandler(sessionCallbackHandler);
    }
}
