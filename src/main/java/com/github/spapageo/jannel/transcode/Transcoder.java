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
import io.netty.buffer.ByteBuf;

public class Transcoder {

    private final TranscoderHelper transcoderHelper;

    public Transcoder(TranscoderHelper transcoderHelper) {
        this.transcoderHelper = transcoderHelper;
    }

    /**
     * Converts a ByteBuffer to a message
     * @param messageType the message type
     * @param byteBuffer the input buffer
     * @return the message that was created
     */
    public Message decode(MessageType messageType, ByteBuf byteBuffer){

        Message message;
        switch (messageType){
            case HEARTBEAT:
                message = transcoderHelper.decodeHeartBeat(byteBuffer);
                break;
            case ADMIN:
                message = transcoderHelper.decodeAdmin(byteBuffer);
                break;
            case SMS:
                message = transcoderHelper.decodeSms(byteBuffer);
                break;
            case ACK:
                message = transcoderHelper.decodeAck(byteBuffer);
                break;
            case DATAGRAM:
                message = transcoderHelper.decodeDatagram(byteBuffer);
                break;
            default:
                throw new UnknownMessageTypeException("Unknown message type");
        }
        return message;
    }

    /**
     * Convert a message to bytes
     * @param message the message to encode
     * @param out the output buffer
     */
    public void encode(Message message, ByteBuf out){
        switch (message.getType()){
            case HEARTBEAT:
                transcoderHelper.encodeHeartBeat((HeartBeat) message, out);
                break;
            case ADMIN:
                transcoderHelper.encodeAdmin((Admin) message, out);
                break;
            case SMS:
                transcoderHelper.encodeSms((Sms) message, out);
                break;
            case ACK:
                transcoderHelper.encodeAck((Ack) message, out);
                break;
            case DATAGRAM:
                transcoderHelper.encodeDatagram((Datagram) message, out);
                break;
            default:
                throw new UnknownMessageTypeException("Unknown message type");
        }
    }
}
