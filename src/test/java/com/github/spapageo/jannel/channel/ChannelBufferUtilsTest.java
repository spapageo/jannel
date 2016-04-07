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

import com.github.spapageo.jannel.exception.InvalidUUIDException;
import com.github.spapageo.jannel.exception.NotEnoughDataDecoderException;
import com.github.spapageo.jannel.exception.StringSizeException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ChannelBufferUtilsTest {

    private static final String STRING_TO_ENCODE = "An preost wes on leoden, Laȝamon was ihoten\n" +
                                                   "He wes Leovenaðes sone -- liðe him be Drihten.\n" +
                                                   "He wonede at Ernleȝe at æðelen are chirechen,\n" +
                                                   "Uppen Sevarne staþe, sel þar him þuhte,\n" +
                                                   "Onfest Radestone, þer he bock radde.";

    @Test(expected = NotEnoughDataDecoderException.class)
    public void testReadOctetStringToStringWhenInputBufferSizeLessThan4ThrowNotEnoughDataException() throws Exception {
        ByteBuf inputBuffer = Unpooled.buffer(1);

        try {
            ChannelBufferUtils.readOctetStringToString(inputBuffer,
                                                       StandardCharsets.UTF_8);
        } finally {
            inputBuffer.release();
        }
    }

    @Test
    public void testReadOctetStringToStringWhenOctetStringSizeIsMinus1ReturnsEmptyString() throws Exception {
        byte[] input = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        ByteBuf inputBuffer = Unpooled.copiedBuffer(input);

        String decodedString = ChannelBufferUtils.readOctetStringToString(inputBuffer,
                                                                          StandardCharsets.UTF_8);

        assertEquals("Decoded string size is not 0", 0, decodedString.length());

        inputBuffer.release();
    }

    @Test(expected = StringSizeException.class)
    public void testReadOctetStringToStringWhenOctetStringSizeIsOtherNegativeNumberThrowsStringSizeException() throws Exception {
        byte[] input = {(byte) 0xff, (byte) 0xff, (byte) 0xff, 0x00};
        ByteBuf inputBuffer = Unpooled.copiedBuffer(input);

        try {
            ChannelBufferUtils.readOctetStringToString(inputBuffer,
                                                       StandardCharsets.UTF_8);

        } finally {
            inputBuffer.release();
        }
    }

    @Test(expected = StringSizeException.class)
    public void testReadOctetStringToStringWhenOctetStringSizeIsGreaterThanBufferSizeThrowsStringSizeException() throws Exception {
        byte[] input = {0x00, 0x00, 0x00, 0x02, 'a'};
        ByteBuf inputBuffer = Unpooled.copiedBuffer(input);

        try {
            ChannelBufferUtils.readOctetStringToString(inputBuffer,
                                                       StandardCharsets.UTF_8);

        } finally {
            inputBuffer.release();
        }
    }


    @Test
    public void testReadOctetStringToString_when_octet_string_size_is_zero_returns_empty_string() throws Exception {
        byte[] input = { 0x00,0x00, 0x00, 0x00};
        ByteBuf inputBuffer = Unpooled.copiedBuffer(input);

        String decodedString = ChannelBufferUtils.readOctetStringToString(inputBuffer,
                                                                          StandardCharsets.UTF_8);

        assertEquals("Decoded string size is not 0", 0, decodedString.length());
        inputBuffer.release();
    }

    @Test
    public void testReadOctetStringToString_when_octet_string_size_is_positive_and_utf8_encoded_reads_string_correctly() throws Exception {


        byte[] stringBytes = STRING_TO_ENCODE.getBytes(StandardCharsets.UTF_8);
        ByteBuf encodedString = Unpooled.buffer(stringBytes.length + 4);
        encodedString.writeInt(stringBytes.length);
        encodedString.writeBytes(stringBytes);

        String decodedString = ChannelBufferUtils.readOctetStringToString(encodedString,
                                                                          StandardCharsets.UTF_8);

        assertEquals("Decoded string and encoded string are not the same",
                     STRING_TO_ENCODE, decodedString);
        encodedString.release();
    }

    @Test
    public void testReadOctetStringToString_when_octet_string_size_is_positive_and_utf16_encoded_reads_string_correctly() throws Exception {

        byte[] stringBytes = STRING_TO_ENCODE.getBytes(StandardCharsets.UTF_16);
        ByteBuf encodedString = Unpooled.buffer(stringBytes.length + 4);
        encodedString.writeInt(stringBytes.length);
        encodedString.writeBytes(stringBytes);

        String decodedString = ChannelBufferUtils.readOctetStringToString(encodedString,
                                                                          StandardCharsets.UTF_16);

        assertEquals("Decoded string and encoded string are not the same",
                     STRING_TO_ENCODE, decodedString);
        encodedString.release();
    }

    @Test(expected = InvalidUUIDException.class)
    public void testReadUUIDWhenInputIsAMalformedUUIDStringThrowsInvalidUUIDException() throws Exception {
        String UUIDString = UUID.randomUUID().toString().replace('-', '1');
        byte[] stringBytes = UUIDString.getBytes(StandardCharsets.UTF_16);
        ByteBuf encodedUUID = Unpooled.buffer(stringBytes.length + 4);
        encodedUUID.writeInt(stringBytes.length);
        encodedUUID.writeBytes(stringBytes);

        try {
            ChannelBufferUtils.readUUID(encodedUUID,
                                        StandardCharsets.UTF_16);
        } finally {
            encodedUUID.release();
        }
    }

    @Test
    public void testReadUUIDWhenInputIsAValidUUIDItIsConvertedToTheSameUUID() throws Exception {
        UUID uuid = UUID.randomUUID();
        String UUIDString = uuid.toString();
        byte[] stringBytes = UUIDString.getBytes(StandardCharsets.UTF_16);
        ByteBuf encodedUUID = Unpooled.buffer(stringBytes.length + 4);
        encodedUUID.writeInt(stringBytes.length);
        encodedUUID.writeBytes(stringBytes);

        UUID readUUID = ChannelBufferUtils.readUUID(encodedUUID,
                                                StandardCharsets.UTF_16);

        assertEquals("Encoded and decoded UUIDs do not match", uuid, readUUID);
    }

    @Test(expected = NotEnoughDataDecoderException.class)
    public void testReadIntWhenInputBufferIsSmallThan4BytesThrowsNotEnoughDataException() throws Exception {
        byte[] input = { 0x00,0x00, 0x00};
        ByteBuf inputBuffer = Unpooled.copiedBuffer(input);
        try {
            ChannelBufferUtils.readInt(inputBuffer);
        } finally {
            inputBuffer.release();
        }
    }

    @Test
    public void testReadIntWhenInputBufferCorrectlyReadInteger() throws Exception {
        byte[] input = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xf0};
        ByteBuf inputBuffer = Unpooled.copiedBuffer(input);

        int readInt = ChannelBufferUtils.readInt(inputBuffer);

        assertEquals("Encoded and decoded ints are not the same", -16, readInt);

        inputBuffer.release();
    }


    @Test(expected = NotEnoughDataDecoderException.class)
    public void testReadOctetStringToBytesWhenInputBufferSizeLessThan4ThrowNotEnoughDataException() throws Exception {
        ByteBuf inputBuffer = Unpooled.buffer(1);

        try {
            ChannelBufferUtils.readOctetStringToBytes(inputBuffer);
        } finally {
            inputBuffer.release();
        }
    }

    @Test
    public void testReadOctetStringToBytesWhenOctetStringSizeIsMinus1ReturnsEmptyString() throws Exception {
        byte[] input = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        ByteBuf inputBuffer = Unpooled.copiedBuffer(input);

        ByteBuf readBytes = ChannelBufferUtils.readOctetStringToBytes(inputBuffer);

        assertEquals("Decoded byte array size is not 0", 0, readBytes.readableBytes());

        inputBuffer.release();
    }

    @Test(expected = StringSizeException.class)
    public void testReadOctetStringToBytesWhenOctetStringSizeIsOtherNegativeNumberThrowsStringSizeException() throws Exception {
        byte[] input = {(byte) 0xff, (byte) 0xff, (byte) 0xff, 0x00};
        ByteBuf inputBuffer = Unpooled.copiedBuffer(input);

        try {
            ChannelBufferUtils.readOctetStringToBytes(inputBuffer);
        } finally {
            inputBuffer.release();
        }
    }

    @Test(expected = StringSizeException.class)
    public void testReadOctetStringToBytesWhenOctetStringSizeIsGreaterThanBufferSizeThrowsStringSizeException() throws Exception {
        byte[] input = {0x00, 0x00, 0x00, 0x02, 0x14};
        ByteBuf inputBuffer = Unpooled.copiedBuffer(input);

        try {
            ChannelBufferUtils.readOctetStringToBytes(inputBuffer);
        } finally {
            inputBuffer.release();
        }
    }


    @Test
    public void testReadOctetStringToBytesWhenOctetStringSizeIsZeroReturnsEmptyArray() throws Exception {
        byte[] input = { 0x00,0x00, 0x00, 0x00};
        ByteBuf inputBuffer = Unpooled.copiedBuffer(input);

        ByteBuf readBytes = ChannelBufferUtils.readOctetStringToBytes(inputBuffer);

        assertEquals("Read byte array size is not 0", 0, readBytes.readableBytes());
        inputBuffer.release();
    }

    @Test
    public void testReadOctetStringToBytesWhenOctetStringSizeIsPositiveReadsByteBufferCorrectly() throws Exception {

        byte[] bytesToTransfer = {0x14, 0x13};
        ByteBuf octetStringBytes = Unpooled.buffer(bytesToTransfer.length + 4);
        octetStringBytes.writeInt(bytesToTransfer.length);
        octetStringBytes.writeBytes(bytesToTransfer);

        ByteBuf readBytes = ChannelBufferUtils.readOctetStringToBytes(octetStringBytes);
        octetStringBytes.readerIndex(4);

        assertEquals("Read bytes and given bytes are not the same",
                   octetStringBytes, readBytes);
        octetStringBytes.release();
        readBytes.release();
    }

    @Test
    public void testWriteStringToOctetStringWhenStringIsNullWritesMinusOne() throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(4);
        ChannelBufferUtils.writeStringToOctetString(null, byteBuf, StandardCharsets.UTF_8);

        assertEquals("Written length prefix in not -1 for null string", -1, byteBuf.readInt());
        byteBuf.release();
    }

    @Test
    public void testWriteStringToOctetStringIsEncodedCorrectly() throws Exception {

        ByteBuf encodedString = Unpooled.copiedBuffer(STRING_TO_ENCODE.getBytes(StandardCharsets.UTF_8));
        ByteBuf outputBuffer = Unpooled.buffer(4 + encodedString.capacity());

        ChannelBufferUtils.writeStringToOctetString(STRING_TO_ENCODE, outputBuffer, StandardCharsets.UTF_8);

        assertEquals("Written length prefix in not the length of the encoded string byte array",
                     encodedString.capacity(), outputBuffer.readInt());

        assertEquals("Written bytes are not the same with those of the input string",
                     encodedString,
                     outputBuffer);
        outputBuffer.release();
        encodedString.release();
    }

    @Test
    public void testWriteBytesToOctetStringWhenBytesAreNullWritesMinusOne() throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(4);
        ChannelBufferUtils.writeBytesToOctetString(null, byteBuf);

        assertEquals("Written length prefix in not -1 for null byte array", -1, byteBuf.readInt());
        byteBuf.release();
    }

    @Test
    public void testWriteBytesToOctetStringIsWrittenCorrectly() throws Exception {

        byte[] payload = { 0x14, 0x16};

        ByteBuf input = Unpooled.copiedBuffer(payload);
        ByteBuf outputBuffer = Unpooled.buffer(4 + payload.length);

        ChannelBufferUtils.writeBytesToOctetString(input, outputBuffer);

        assertEquals("Written length prefix in not the length of the encoded byte array",
                     payload.length,
                     outputBuffer.readInt());

        assertEquals("Written bytes are not the same with those of the input array",
                     input.readerIndex(0),
                     outputBuffer);
        outputBuffer.release();
        input.release();
    }

    @Test
    public void testWriteUUIDToOctetStringWhenStringIsNullWritesMinusOne() throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(4);
        ChannelBufferUtils.writeUUIDToOctetString(null, byteBuf, StandardCharsets.UTF_8);

        assertEquals("Written length prefix in not -1 for null string", -1, byteBuf.readInt());
        byteBuf.release();
    }

    @Test
    public void testWriteUUIDToOctetStringIsEncodedCorrectly() throws Exception {

        UUID input = UUID.randomUUID();
        String uuidString = input.toString();
        ByteBuf encodedUUID = Unpooled.copiedBuffer(uuidString.getBytes(StandardCharsets.UTF_8));
        ByteBuf outputBuffer = Unpooled.buffer(4 + encodedUUID.capacity());

        ChannelBufferUtils.writeUUIDToOctetString(input, outputBuffer, StandardCharsets.UTF_8);

        assertEquals("Written length prefix in not the length of the encoded string byte array",
                     encodedUUID.capacity(), outputBuffer.readInt());

        assertEquals("Written bytes are not the same with those of the input string",
                     encodedUUID,
                     outputBuffer);
        outputBuffer.release();
        encodedUUID.release();
    }
}