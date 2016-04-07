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

package com.github.spapageo.jannel.msg;

import com.github.spapageo.jannel.msg.enums.SmsConstants;
import io.netty.buffer.ByteBuf;

/**
 * A data packet
 */
public class Datagram implements Message{

    /**
     * The source address
     */
    private String sourceAddress;

    /**
     * The source port
     */
    private int sourcePort = SmsConstants.PARAM_UNDEFINED;

    /**
     * The destination address
     */
    private String destinationAddress;

    /**
     * The destination port
     */
    private int destinationPort = SmsConstants.PARAM_UNDEFINED;

    /**
     * The data of the datagram
     */
    private ByteBuf userData;

    /**
     * @return the source address of this datagram
     */
    public String getSourceAddress() {
        return sourceAddress;
    }

    /**
     * Sets the source address of this datagram
     * @param sourceAddress the new source address
     */
    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    /**
     * @return the source port of this datagram
     */
    public int getSourcePort() {
        return sourcePort;
    }

    /**
     * Sets the source port of this datagram
     * @param sourcePort the new port
     */
    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    /**
     * @return the destination address of this datagram
     */
    public String getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * Sets the destination address of this datagram
     * @param destinationAddress the new destination address
     */
    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    /**
     * @return the destination port of this datagram
     */
    public int getDestinationPort() {
        return destinationPort;
    }

    /**
     * Sets the new destination port of this datagram
     * @param destinationPort the new destination port
     */
    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    /**
     * @return the user data
     */
    public ByteBuf getUserData() {
        return userData;
    }

    /**
     * Sets the user data of this datagram
     * @param userData the new user data
     */
    public void setUserData(ByteBuf userData) {
        this.userData = userData;
    }

    @Override
    public MessageType getType() {
        return MessageType.DATAGRAM;
    }

    @Override
    public String toString() {
        return "Datagram{" +
               "sourceAddress='" + sourceAddress + '\'' +
               ", sourcePort=" + sourcePort +
               ", destinationAddress='" + destinationAddress + '\'' +
               ", destinationPort=" + destinationPort +
               '}';
    }
}
