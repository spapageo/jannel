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

import com.github.spapageo.jannel.channel.ChannelBufferUtils;
import com.github.spapageo.jannel.exception.UnknownAckTypeException;
import com.github.spapageo.jannel.exception.UnknownAdminCommandException;
import com.github.spapageo.jannel.exception.UnknownSmsTypeException;
import com.github.spapageo.jannel.msg.*;
import com.github.spapageo.jannel.msg.enums.*;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TranscoderHelper {

    Message decodeHeartBeat(ByteBuf byteBuffer) {
        return new HeartBeat(ChannelBufferUtils.readInt(byteBuffer));
    }

    Message decodeAdmin(ByteBuf byteBuffer) {
        Admin adminCommand = new Admin();
        adminCommand.setAdminCommand(AdminCommand.fromValue(ChannelBufferUtils.readInt(byteBuffer)));
        adminCommand.setBoxId(ChannelBufferUtils.readOctetStringToString(byteBuffer,
                                                                         StandardCharsets.UTF_8));
        if (adminCommand.getAdminCommand() == AdminCommand.ADMIN_UNDEF)
            throw new UnknownAdminCommandException("Unknown admin command");
        return adminCommand;
    }

    Message decodeDatagram(ByteBuf byteBuffer) {
        Datagram datagram = new Datagram();
        datagram.setSourceAddress(ChannelBufferUtils.readOctetStringToString(byteBuffer,
                                                                             StandardCharsets.UTF_8));
        datagram.setSourcePort(ChannelBufferUtils.readInt(byteBuffer));
        datagram.setDestinationAddress(ChannelBufferUtils.readOctetStringToString(byteBuffer,
                                                                                  StandardCharsets.UTF_8));
        datagram.setDestinationPort(ChannelBufferUtils.readInt(byteBuffer));
        datagram.setUserData(ChannelBufferUtils.readOctetStringToBytes(byteBuffer));
        return datagram;
    }

    Message decodeAck(ByteBuf byteBuffer) {
        Ack ack = new Ack();
        ack.setResponse(AckType.fromValue(ChannelBufferUtils.readInt(byteBuffer)));
        ack.setTime(ChannelBufferUtils.readInt(byteBuffer));
        ack.setId(ChannelBufferUtils.readUUID(byteBuffer, StandardCharsets.UTF_8));
        if (ack.getResponse() == AckType.ACK_UNDEF)
            throw new UnknownAckTypeException("Unknown ack type");
        return ack;
    }

    Message decodeSms(ByteBuf byteBuffer) {
        Sms sms = new Sms();
        sms.setSender(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        sms.setReceiver(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        sms.setUdhData(ChannelBufferUtils.readOctetStringToBytes(byteBuffer));
        sms.setMsgData(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        sms.setTime(ChannelBufferUtils.readInt(byteBuffer));
        sms.setSmscId(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        sms.setSmscNumber(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        sms.setForeignId(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        sms.setService(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        sms.setAccount(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        sms.setId(ChannelBufferUtils.readUUID(byteBuffer, StandardCharsets.UTF_8));
        sms.setSmsType(SmsType.fromValue(ChannelBufferUtils.readInt(byteBuffer)));
        sms.setMessageClass(MessageClass.fromValue(ChannelBufferUtils.readInt(byteBuffer)));
        sms.setMwi(MessageWaitingIndicator.fromValue(ChannelBufferUtils.readInt(byteBuffer)));
        sms.setCoding(DataCoding.fromValue(ChannelBufferUtils.readInt(byteBuffer)));
        sms.setCompress(Compress.fromValue(ChannelBufferUtils.readInt(byteBuffer)));
        sms.setValidity(ChannelBufferUtils.readInt(byteBuffer));
        sms.setDeferred(ChannelBufferUtils.readInt(byteBuffer));
        sms.setDlrMask(ChannelBufferUtils.readInt(byteBuffer));
        sms.setDlrUrl(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        sms.setPid(ChannelBufferUtils.readInt(byteBuffer));
        sms.setAltDcs(ChannelBufferUtils.readInt(byteBuffer));
        sms.setRpi(ReturnPathIndicator.fromValue(ChannelBufferUtils.readInt(byteBuffer)));
        sms.setCharset(Charset.availableCharsets().get(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8)));
        sms.setBoxId(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        sms.setBillingInfo(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        sms.setMsgLeft(ChannelBufferUtils.readInt(byteBuffer));
        sms.setPriority(ChannelBufferUtils.readInt(byteBuffer));
        sms.setResendTry(ChannelBufferUtils.readInt(byteBuffer));
        sms.setResendTime(ChannelBufferUtils.readInt(byteBuffer));
        sms.setMetaData(ChannelBufferUtils.readOctetStringToString(byteBuffer, StandardCharsets.UTF_8));
        if (sms.getSmsType() == SmsType.SMS_UNDEF)
            throw new UnknownSmsTypeException("Unknown ack type");
        return sms;
    }

    void encodeDatagram(Datagram datagram, ByteBuf out) {
        ChannelBufferUtils.writeStringToOctetString(datagram.getSourceAddress(),
                                                    out,
                                                    StandardCharsets.UTF_8);
        out.writeInt(datagram.getSourcePort());
        ChannelBufferUtils.writeStringToOctetString(datagram.getDestinationAddress(),
                                                    out,
                                                    StandardCharsets.UTF_8);
        out.writeInt(datagram.getDestinationPort());
        ChannelBufferUtils.writeBytesToOctetString(datagram.getUserData(), out);
    }

    void encodeAck(Ack ack, ByteBuf out) {
        out.writeInt(ack.getResponse().value());
        out.writeInt(ack.getTime());
        ChannelBufferUtils.writeStringToOctetString(ack.getId().toString(),
                                                    out,
                                                    StandardCharsets.UTF_8);
    }

    void encodeHeartBeat(HeartBeat heartBeat, ByteBuf out) {
        out.writeInt(heartBeat.getLoad());
    }

    void encodeAdmin(Admin admin, ByteBuf out) {
        out.writeInt(admin.getAdminCommand().value());
        ChannelBufferUtils.writeStringToOctetString(admin.getBoxId(),
                                                    out,
                                                    StandardCharsets.UTF_8);
    }

    void encodeSms(Sms sms, ByteBuf out) {
        Charset messageCharset = sms.getCharset() == null ? StandardCharsets.UTF_8 : sms.getCharset();

        ChannelBufferUtils.writeStringToOctetString(sms.getSender(), out, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString(sms.getReceiver(), out, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeBytesToOctetString(sms.getUdhData(), out);
        ChannelBufferUtils.writeStringToOctetString(sms.getMsgData(), out, messageCharset);
        out.writeInt(sms.getTime());
        ChannelBufferUtils.writeStringToOctetString(sms.getSmscId(), out, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString(sms.getSmscNumber(), out, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString(sms.getForeignId(), out, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString(sms.getService(), out, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString(sms.getAccount(), out, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeUUIDToOctetString(sms.getId(), out, StandardCharsets.UTF_8);
        out.writeInt(sms.getSmsType().value());
        out.writeInt(sms.getMessageClass().value());
        out.writeInt(sms.getMwi().value());
        out.writeInt(sms.getCoding().value());
        out.writeInt(sms.getCompress().value());
        out.writeInt(sms.getValidity());
        out.writeInt(sms.getDeferred());
        out.writeInt(sms.getDlrMask());
        ChannelBufferUtils.writeStringToOctetString(sms.getDlrUrl(), out, StandardCharsets.UTF_8);
        out.writeInt(sms.getPid());
        out.writeInt(sms.getAltDcs());
        out.writeInt(sms.getRpi().value());
        ChannelBufferUtils.writeStringToOctetString(messageCharset.displayName(), out, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString(sms.getBoxId(), out, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString(sms.getBillingInfo(), out, StandardCharsets.UTF_8);
        out.writeInt(sms.getMsgLeft());
        out.writeInt(sms.getPriority());
        out.writeInt(sms.getResendTry());
        out.writeInt(sms.getResendTime());
        ChannelBufferUtils.writeStringToOctetString(sms.getMetaData(), out, StandardCharsets.UTF_8);
    }
}