/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Spyridon Papageorgiou
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

package com.github.spapageo.jannel.transcode;

import com.github.spapageo.jannel.msg.Message;
import com.github.spapageo.jannel.msg.MessageType;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;

/**
 * Decodes messages from bytes of a {@link ByteBuf}
 */
public interface Decoder {
    /**
     * Converts a ByteBuffer to a message
     * @param messageType the message type
     * @param byteBuffer the input buffer
     * @return the message that was created
     */
    @Nonnull
    Message decode(MessageType messageType, ByteBuf byteBuffer);
}
