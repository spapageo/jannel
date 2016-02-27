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
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TranscoderHelperTest {

    TranscoderHelper transcoderHelper = new TranscoderHelper();

    @Test
    public void testDecodeHeartBeatDecodesCorrectly() throws Exception {
        ByteBuf encodedMessage = Unpooled.buffer();
        encodedMessage.writeInt(124);

        HeartBeat heartBeat = (HeartBeat) transcoderHelper.decodeHeartBeat(encodedMessage);
        assertEquals("HeartBeat load is incorrect", 124, heartBeat.getLoad());
        encodedMessage.release();
    }

    @Test
    public void testDecodeAdminDecodesCorrectly() throws Exception {
        ByteBuf encodedMessage = Unpooled.buffer();
        encodedMessage.writeInt(1);
        ChannelBufferUtils.writeStringToOctetString("γεια", encodedMessage, StandardCharsets.UTF_8);

        Admin admin = (Admin) transcoderHelper.decodeAdmin(encodedMessage);
        Assert.assertEquals("Type of admin command is incorrect", AdminCommand.SUSPEND, admin.getAdminCommand());
        assertEquals("Box name is incorrect", "γεια", admin.getBoxId());
        encodedMessage.release();
    }

    @Test(expected = UnknownAdminCommandException.class)
    public void testDecodeAdminThrowsWhenAdminCommandIsUnkwown() throws Exception {
        ByteBuf encodedMessage = Unpooled.buffer();
        encodedMessage.writeInt(5);
        ChannelBufferUtils.writeStringToOctetString("γεια", encodedMessage, StandardCharsets.UTF_8);

        try{
            transcoderHelper.decodeAdmin(encodedMessage);
        } finally {
            encodedMessage.release();
        }
    }

    @Test
    public void testDecodeDatagramDecodesCorrectly() throws Exception {
        ByteBuf encodedMessage = Unpooled.buffer();
        ChannelBufferUtils.writeStringToOctetString("απο",
                                                    encodedMessage,
                                                    StandardCharsets.UTF_8);
        encodedMessage.writeInt(4444);
        ChannelBufferUtils.writeStringToOctetString("προς",
                                                    encodedMessage,
                                                    StandardCharsets.UTF_8);
        encodedMessage.writeInt(5555);
        byte[] payload = { 0x14, 0x79};
        ChannelBufferUtils.writeBytesToOctetString(payload, encodedMessage);

        Datagram datagram = (Datagram) transcoderHelper.decodeDatagram(encodedMessage);
        assertEquals("Source address is incorrect", "απο", datagram.getSourceAddress());
        assertEquals("Source port is incorrect", 4444, datagram.getSourcePort());
        assertEquals("Source address is incorrect", "προς", datagram.getDestinationAddress());
        assertEquals("Source port is incorrect", 5555, datagram.getDestinationPort());
        assertArrayEquals("Data payload is incorrect", payload, datagram.getUserData());
        encodedMessage.release();
    }

    @Test
    public void testDecodeAckDecodesCorrectly() throws Exception {
        ByteBuf encodedMessage = Unpooled.buffer();
        encodedMessage.writeInt(1);
        encodedMessage.writeInt(9);
        UUID uuid = UUID.randomUUID();
        ChannelBufferUtils.writeUUIDToOctetString(uuid, encodedMessage, StandardCharsets.UTF_8);

        Ack ack = (Ack) transcoderHelper.decodeAck(encodedMessage);
        Assert.assertEquals("Type of ack response is incorrect", AckType.FAILED, ack.getResponse());
        assertEquals("The time is incorrect", 9, ack.getTime());
        assertEquals("Ack id is incorrect", uuid, ack.getId());
        encodedMessage.release();
    }

    @Test(expected = UnknownAckTypeException.class)
    public void testDecodeAckThrowsWhenAckTypeIsUnknown() throws Exception {
        ByteBuf encodedMessage = Unpooled.buffer();
        encodedMessage.writeInt(111);
        encodedMessage.writeInt(9);
        UUID uuid = UUID.randomUUID();
        ChannelBufferUtils.writeUUIDToOctetString(uuid, encodedMessage, StandardCharsets.UTF_8);

        try {
            transcoderHelper.decodeAck(encodedMessage);
        } finally {
            encodedMessage.release();
        }
    }

    @Test
    public void testDecodeSmsDecodesCorrectly() throws Exception {
        ByteBuf encodedMessage = Unpooled.buffer();
        UUID uuid = UUID.randomUUID();
        ChannelBufferUtils.writeStringToOctetString("from", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("to", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("udhdata", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("content", encodedMessage, StandardCharsets.UTF_8);
        encodedMessage.writeInt(0);
        ChannelBufferUtils.writeStringToOctetString("smsc", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("smscNumber", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("foreignId", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("service", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("account", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeUUIDToOctetString(uuid, encodedMessage, StandardCharsets.UTF_8);
        encodedMessage.writeInt(1);
        encodedMessage.writeInt(2);
        encodedMessage.writeInt(3);
        encodedMessage.writeInt(4);
        encodedMessage.writeInt(5);
        encodedMessage.writeInt(6);
        encodedMessage.writeInt(7);
        encodedMessage.writeInt(8);
        ChannelBufferUtils.writeStringToOctetString("dlrUrl", encodedMessage, StandardCharsets.UTF_8);
        encodedMessage.writeInt(9);
        encodedMessage.writeInt(10);
        encodedMessage.writeInt(11);
        ChannelBufferUtils.writeStringToOctetString("charset", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("boxcid", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("binfo", encodedMessage, StandardCharsets.UTF_8);
        encodedMessage.writeInt(12);
        encodedMessage.writeInt(13);
        encodedMessage.writeInt(14);
        encodedMessage.writeInt(15);
        ChannelBufferUtils.writeStringToOctetString("metaData", encodedMessage, StandardCharsets.UTF_8);

        Sms sms = (Sms) transcoderHelper.decodeSms(encodedMessage);
        assertEquals("The  from is incorrect", "from", sms.getSender());
        assertEquals("The to is incorrect", "to", sms.getReceiver());
        assertEquals("The udhdata is incorrect", "udhdata", sms.getUdhData());
        assertEquals("The message data is incorrect", "content", sms.getMsgData());
        assertEquals("The time is incorrect", 0, sms.getTime());
        assertEquals("The smsc is incorrect", "smsc", sms.getSmscId());
        assertEquals("The smscNumber is incorrect", "smscNumber", sms.getSmscNumber());
        assertEquals("The foreignId is incorrect", "foreignId", sms.getForeignId());
        assertEquals("The service is incorrect", "service", sms.getService());
        assertEquals("The account is incorrect", "account", sms.getAccount());
        assertEquals("The id is incorrect", uuid, sms.getId());
        assertEquals("The sms type is incorrect", 1, sms.getSmsType().value());
        assertEquals("The m class is incorrect", 2, sms.getmClass());
        assertEquals("The mwi is incorrect", 3, sms.getMwi());
        assertEquals("The coding is incorrect", 4, sms.getCoding());
        assertEquals("The compress is incorrect", 5, sms.getCompress());
        assertEquals("The validity is incorrect", 6, sms.getValidity());
        assertEquals("The deferred is incorrect", 7, sms.getDeferred());
        assertEquals("The dlr mask is incorrect", 8, sms.getDlrMask());
        assertEquals("The dlr url is incorrect", "dlrUrl", sms.getDlrUrl());

        assertEquals("The pid is incorrect", 9, sms.getPid());
        assertEquals("The alt dcs is incorrect", 10, sms.getAltDcs());
        assertEquals("The rpi is incorrect", 11, sms.getRpi());
        assertEquals("The charset is incorrect", "charset", sms.getCharset());
        assertEquals("The box id is incorrect", "boxcid", sms.getBoxcId());
        assertEquals("The binfo is incorrect", "binfo", sms.getBinfo());

        assertEquals("The msgLeft is incorrect", 12, sms.getMsgLeft());
        assertEquals("The priority is incorrect", 13, sms.getPriority());
        assertEquals("The resend try is incorrect", 14, sms.getResendTry());
        assertEquals("The resend time is incorrect", 15, sms.getResendTime());
        assertEquals("The meta data is incorrect", "metaData", sms.getMetaData());

        encodedMessage.release();
    }

    @Test(expected = UnknownSmsTypeException.class)
    public void testDecodeSmsThrowsWhenSmsTypeIsInvalid() throws Exception {
        ByteBuf encodedMessage = Unpooled.buffer();
        UUID uuid = UUID.randomUUID();
        ChannelBufferUtils.writeStringToOctetString("from", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("to", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("udhdata", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("content", encodedMessage, StandardCharsets.UTF_8);
        encodedMessage.writeInt(0);
        ChannelBufferUtils.writeStringToOctetString("smsc", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("smscNumber", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("foreignId", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("service", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("account", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeUUIDToOctetString(uuid, encodedMessage, StandardCharsets.UTF_8);
        encodedMessage.writeInt(155);
        encodedMessage.writeInt(2);
        encodedMessage.writeInt(3);
        encodedMessage.writeInt(4);
        encodedMessage.writeInt(5);
        encodedMessage.writeInt(6);
        encodedMessage.writeInt(7);
        encodedMessage.writeInt(8);
        ChannelBufferUtils.writeStringToOctetString("dlrUrl", encodedMessage, StandardCharsets.UTF_8);
        encodedMessage.writeInt(9);
        encodedMessage.writeInt(10);
        encodedMessage.writeInt(11);
        ChannelBufferUtils.writeStringToOctetString("charset", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("boxcid", encodedMessage, StandardCharsets.UTF_8);
        ChannelBufferUtils.writeStringToOctetString("binfo", encodedMessage, StandardCharsets.UTF_8);
        encodedMessage.writeInt(12);
        encodedMessage.writeInt(13);
        encodedMessage.writeInt(14);
        encodedMessage.writeInt(15);
        ChannelBufferUtils.writeStringToOctetString("metaData", encodedMessage, StandardCharsets.UTF_8);

        try {
            transcoderHelper.decodeSms(encodedMessage);
        } finally {
            encodedMessage.release();
        }

    }


    @Test
    public void testEncodeDatagramEncodesCorrectly() throws Exception {
        byte[] payload = {0x56, 0x35};
        Datagram datagram = new Datagram();
        datagram.setDestinationAddress("προς");
        datagram.setSourceAddress("απο");
        datagram.setDestinationPort(5555);
        datagram.setSourcePort(4444);
        datagram.setUserData(payload);
        ByteBuf byteBuf = Unpooled.buffer();

        transcoderHelper.encodeDatagram(datagram, byteBuf);
        assertEquals("Source address is incorrect", "απο", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                      StandardCharsets.UTF_8));
        assertEquals("Source port is incorrect", 4444, byteBuf.readInt());
        assertEquals("Source address is incorrect", "προς", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                       StandardCharsets.UTF_8));
        assertEquals("Source port is incorrect", 5555, byteBuf.readInt());
        assertArrayEquals("Data payload is incorrect", payload, ChannelBufferUtils.readOctetStringToBytes(byteBuf));

        byteBuf.release();
    }

    @Test
    public void testEncodeAckEncodesCorrectly() throws Exception {
        Ack ack = new Ack();
        ack.setTime(0);
        ack.setId(UUID.randomUUID());
        ack.setResponse(AckType.SUCCESS);
        ByteBuf byteBuf = Unpooled.buffer();
        transcoderHelper.encodeAck(ack, byteBuf);

        assertEquals("Type of ack response is incorrect", AckType.SUCCESS, AckType.fromValue(byteBuf.readInt()));
        assertEquals("The time is incorrect", 0, byteBuf.readInt());
        assertEquals("Ack id is incorrect", ack.getId(), ChannelBufferUtils.readUUID(byteBuf,
                                                                                     StandardCharsets.UTF_8));

        byteBuf.release();
    }

    @Test
    public void testEncodeHeartBeatEncodesCorrectly() throws Exception {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setLoad(1);
        ByteBuf byteBuf = Unpooled.buffer();
        transcoderHelper.encodeHeartBeat(heartBeat, byteBuf);

        assertEquals("HeartBeat load is incorrect", 1, byteBuf.readInt());
        byteBuf.release();
    }

    @Test
    public void testEncodeAdminEncodesCorrectly() throws Exception {
        Admin admin = new Admin();
        admin.setAdminCommand(AdminCommand.IDENTIFY);
        admin.setBoxId("γεια");
        ByteBuf byteBuf = Unpooled.buffer();
        transcoderHelper.encodeAdmin(admin, byteBuf);

        assertEquals("Type of admin command is incorrect", AdminCommand.IDENTIFY, AdminCommand.fromValue(byteBuf.readInt()));
        assertEquals("Box name is incorrect", "γεια", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                 StandardCharsets.UTF_8));
        byteBuf.release();
    }

    @Test
    public void testEncodeSmsEncodesCorrectly() throws Exception {
        Sms sms = new Sms();
        sms.setSender("from");
        sms.setReceiver("to");
        sms.setUdhData("udhdata");
        sms.setMsgData("content");
        sms.setTime(0);
        sms.setSmscId("smsc");
        sms.setSmscNumber("smscNumber");
        sms.setForeignId("foreignId");
        sms.setService("service");
        sms.setAccount("account");
        sms.setId(UUID.randomUUID());
        sms.setSmsType(SmsType.MOBILE_TERMINATED_REPLY);
        sms.setmClass(2);
        sms.setMwi(3);
        sms.setCoding(4);
        sms.setCompress(5);
        sms.setValidity(6);
        sms.setDeferred(7);
        sms.setDlrMask(8);
        sms.setDlrUrl("dlrUrl");
        sms.setPid(9);
        sms.setAltDcs(10);
        sms.setRpi(11);
        sms.setCharset("charset");
        sms.setBoxcId("box");
        sms.setBinfo("binfo");
        sms.setMsgLeft(12);
        sms.setPriority(13);
        sms.setResendTry(14);
        sms.setResendTime(15);
        sms.setMetaData("metadata");
        ByteBuf byteBuf = Unpooled.buffer();
        transcoderHelper.encodeSms(sms, byteBuf);


        assertEquals("The  from is incorrect", "from", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                  StandardCharsets.UTF_8));
        assertEquals("The to is incorrect", "to", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                             StandardCharsets.UTF_8));
        assertEquals("The udhdata is incorrect", "udhdata", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                       StandardCharsets.UTF_8));
        assertEquals("The message data is incorrect", "content", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                       StandardCharsets.UTF_8));

        assertEquals("The time is incorrect", 0, byteBuf.readInt());
        assertEquals("The smsc is incorrect", "smsc", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                 StandardCharsets.UTF_8));
        assertEquals("The smscNumber is incorrect", "smscNumber", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                             StandardCharsets.UTF_8));
        assertEquals("The foreignId is incorrect", "foreignId", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                           StandardCharsets.UTF_8));
        assertEquals("The service is incorrect", "service", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                       StandardCharsets.UTF_8));
        assertEquals("The account is incorrect", "account", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                       StandardCharsets.UTF_8));
        assertEquals("The id is incorrect", sms.getId(), ChannelBufferUtils.readUUID(byteBuf,
                                                                              StandardCharsets.UTF_8));
        assertEquals("The sms type is incorrect", sms.getSmsType(), SmsType.fromValue(byteBuf.readInt()));
        assertEquals("The m class is incorrect", 2, byteBuf.readInt());
        assertEquals("The mwi is incorrect", 3, byteBuf.readInt());
        assertEquals("The coding is incorrect", 4, byteBuf.readInt());
        assertEquals("The compress is incorrect", 5, byteBuf.readInt());
        assertEquals("The validity is incorrect", 6, byteBuf.readInt());
        assertEquals("The deferred is incorrect", 7, byteBuf.readInt());
        assertEquals("The dlr mask is incorrect", 8, byteBuf.readInt());
        assertEquals("The dlr url is incorrect", "dlrUrl", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                      StandardCharsets.UTF_8));

        assertEquals("The pid is incorrect", 9, byteBuf.readInt());
        assertEquals("The alt dcs is incorrect", 10, byteBuf.readInt());
        assertEquals("The rpi is incorrect", 11, byteBuf.readInt());
        assertEquals("The charset is incorrect", "charset", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                       StandardCharsets.UTF_8));
        assertEquals("The box id is incorrect", "box", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                     StandardCharsets.UTF_8));
        assertEquals("The binfo is incorrect", "binfo", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                   StandardCharsets.UTF_8));

        assertEquals("The msgLeft is incorrect", 12, byteBuf.readInt());
        assertEquals("The priority is incorrect", 13, byteBuf.readInt());
        assertEquals("The resend try is incorrect", 14, byteBuf.readInt());
        assertEquals("The resend time is incorrect", 15, byteBuf.readInt());
        assertEquals("The meta data is incorrect", "metadata", ChannelBufferUtils.readOctetStringToString(byteBuf,
                                                                                                          StandardCharsets.UTF_8));
    }
}