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

package com.github.spapageo.jannel.transcode;

import com.github.spapageo.jannel.exception.UnknownMessageTypeException;
import com.github.spapageo.jannel.msg.*;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TranscoderTest {

    @Test
    public void testDecodeWhenMessageTypeIsAdminCallsDecodeAdmin() throws Exception {
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        assertNull(transcoder.decode(MessageType.ADMIN, null));
        verify(transcoderHelper).decodeAdmin(null);
    }

    @Test
    public void testDecodeWhenMessageTypeIsAckCallsDecodeAck() throws Exception {
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        assertNull(transcoder.decode(MessageType.ACK, null));
        verify(transcoderHelper).decodeAck(null);
    }

    @Test
    public void testDecodeWhenMessageTypeIsSmsCallsDecodeSms() throws Exception {
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        assertNull(transcoder.decode(MessageType.SMS, null));
        verify(transcoderHelper).decodeSms(null);
    }

    @Test
    public void testDecodeWhenMessageTypeIsHeartBeatCallsDecodeHeartBeat() throws Exception {
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        assertNull(transcoder.decode(MessageType.HEARTBEAT, null));
        verify(transcoderHelper).decodeHeartBeat(null);
    }

    @Test
    public void testDecodeWhenMessageTypeIsDatagramCallsDecodeDatagram() throws Exception {
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        assertNull(transcoder.decode(MessageType.DATAGRAM, null));
        verify(transcoderHelper).decodeDatagram(null);
    }

    @Test(expected = UnknownMessageTypeException.class)
    public void testDecodeWhenMessageTypeIsUnknownThrowsUnknownMessageTypeException() throws Exception {
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        transcoder.decode(MessageType.UNKNOWN, null);
    }

    @Test
    public void testEncodeWhenSmsCallEncodeSms() throws Exception {
        Sms sms = new Sms();
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        transcoder.encode(sms, null);
        verify(transcoderHelper).encodeSms(sms, null);
    }

    @Test
    public void testEncodeWhenAdminCallEncodeAdmin() throws Exception {
        Admin admin = new Admin();
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        transcoder.encode(admin, null);
        verify(transcoderHelper).encodeAdmin(admin, null);
    }

    @Test
    public void testEncodeWhenHeartBeatCallEncodeHeartBeat() throws Exception {
        HeartBeat heartBeat = new HeartBeat();
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        transcoder.encode(heartBeat, null);
        verify(transcoderHelper).encodeHeartBeat(heartBeat, null);
    }

    @Test
    public void testEncodeWhenAckCallEncodeAck() throws Exception {
        Ack ack = new Ack();
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        transcoder.encode(ack, null);
        verify(transcoderHelper).encodeAck(ack, null);
    }

    @Test
    public void testEncodeWhenDatagramCallEncodeDatagram() throws Exception {
        Datagram datagram = new Datagram();
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        transcoder.encode(datagram, null);
        verify(transcoderHelper).encodeDatagram(datagram, null);
    }

    @Test(expected = UnknownMessageTypeException.class)
    public void testEncodeWhenUnknownThrowsUnknownMessageTypeException() throws Exception {
        Message message = new Message() {
            @Override
            public MessageType getType() {
                return MessageType.UNKNOWN;
            }
        };
        TranscoderHelper transcoderHelper = mock(TranscoderHelper.class);

        Transcoder transcoder = new DefaultTranscoder(transcoderHelper);

        transcoder.encode(message, null);
    }
}