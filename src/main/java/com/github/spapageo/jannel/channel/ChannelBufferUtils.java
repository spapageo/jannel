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

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Helper functions for reading and writing to byte buffers
 */
public final class ChannelBufferUtils {

    private static final String EMPTY_STRING = "";

    private ChannelBufferUtils() { }

    /**
     * Reads a new octet string from the byte buffer using the UTF-8 encoding
     * @param byteBuffer the bytes to read the string from
     * @param charset    the charset to use in order to decode the string
     * @return the created string
     */
    public static String readOctetStringToString(ByteBuf byteBuffer, Charset charset){

        if(byteBuffer.readableBytes() < 4)
            throw new NotEnoughDataDecoderException("Not enough bytes to read the octet string size");

        int stringSize = byteBuffer.readInt();

        checkOctetStringSize(byteBuffer, stringSize);

        if(stringSize == -1)
            return EMPTY_STRING;

        return byteBuffer.readSlice(stringSize).toString(charset);
    }

    /**
     * Read a UUID from the byte buffer in the form of a string
     * @param byteBuffer the bytes to read from
     * @param charset    the charset to use in order to decode the string uuid
     * @return the UUID
     */
    public static UUID readUUID(ByteBuf byteBuffer, Charset charset){
        try{
            return UUID.fromString(readOctetStringToString(byteBuffer, charset));
        } catch (IllegalArgumentException exception){
            throw new InvalidUUIDException("The input is not a valid UUID", exception);
        }
    }

    /**
     * Read a 4 byte integer from the byte buffer
     * @param byteBuffer the bytes to read from
     * @return the integer
     */
    public static int readInt(ByteBuf byteBuffer){
        if(byteBuffer.readableBytes() < 4)
            throw new NotEnoughDataDecoderException("Not enough bytes to read the integer");

        return byteBuffer.readInt();
    }

    /**
     * Reads a data segment from the byte buffer
     * @param byteBuffer the bytes to read from
     * @return the data segment
     */
    public static ByteBuf readOctetStringToBytes(ByteBuf byteBuffer){
        if(byteBuffer.readableBytes() < 4)
            throw new NotEnoughDataDecoderException("Not enough bytes to read the octet string size");

        int dataSize = byteBuffer.readInt();

        if(dataSize == -1)
            return Unpooled.EMPTY_BUFFER;

        checkOctetStringSize(byteBuffer, dataSize);

        return byteBuffer.readBytes(dataSize);
    }

    /**
     * Writes a string to output buffer using the specified encoding
     * @param input the input string
     * @param output the output buffer
     * @param charset the charset to use in order to encode the string
     */
    public static void writeStringToOctetString(String input, ByteBuf output, Charset charset){
        if(input == null){
            output.writeInt(-1);
            return;
        }

        byte[] bytes = input.getBytes(charset);
        output.writeInt(bytes.length);
        output.writeBytes(bytes);
    }

    /**
     * Writes a byte array to output buffer
     * @param input the input byte array
     * @param output the output buffer
     */
    public static void writeBytesToOctetString(ByteBuf input, ByteBuf output){
        if(input == null){
            output.writeInt(-1);
            return;
        }

        output.writeInt(input.readableBytes());
        output.writeBytes(input);
    }

    /**
     * Write a UUID as a string to the output buffer
     * @param id the input uuid
     * @param out the output buffer
     * @param charset the charset to use in order to serialize the id
     */
    public static void writeUUIDToOctetString(UUID id, ByteBuf out, Charset charset) {
        String stringFormat =  id == null ? null : id.toString();

        writeStringToOctetString(stringFormat, out, charset);
    }

    private static void checkOctetStringSize(ByteBuf byteBuffer, int stringSize) {
        if(stringSize < -1 || stringSize > byteBuffer.readableBytes())
            throw new StringSizeException("The octet string size read was " + stringSize +
                                          " and the readable bytes were " + byteBuffer.readableBytes());
    }
}
