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

import com.github.spapageo.jannel.msg.Ack;
import com.github.spapageo.jannel.msg.Message;
import com.github.spapageo.jannel.msg.MessageType;
import com.github.spapageo.jannel.transcode.DefaultTranscoder;
import com.github.spapageo.jannel.transcode.Transcoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MessageDecoderTest {

    @Test
    public void testDecodeReadsMessageTypeAndCallTranscoder() throws Exception {
        Transcoder transcoder = mock(DefaultTranscoder.class);

        MessageDecoder messageDecoder = new MessageDecoder(transcoder);

        Message msg = new Ack();
        ByteBuf byteBuf = Unpooled.buffer(4);
        byteBuf.writeInt(msg.getType().value());

        List<Object> output = new ArrayList<Object>();

        messageDecoder.decode(null, byteBuf, output);

        verify(transcoder).decode(MessageType.ACK, byteBuf);

        byteBuf.release();
    }

    @Test
    public void testDecodeAddsDecodedMessageToOutputList() throws Exception {
        Transcoder transcoder = mock(DefaultTranscoder.class);

        MessageDecoder messageDecoder = new MessageDecoder(transcoder);

        Message msg = new Ack();
        ByteBuf byteBuf = Unpooled.buffer(4);
        byteBuf.writeInt(msg.getType().value());

        List<Object> output = new ArrayList<Object>();

        when(transcoder.decode(any(MessageType.class), any(ByteBuf.class))).thenReturn(msg);

        messageDecoder.decode(null, byteBuf, output);

        assertTrue("List doesn't contain the decoded message", output.contains(msg));

        byteBuf.release();
    }
}