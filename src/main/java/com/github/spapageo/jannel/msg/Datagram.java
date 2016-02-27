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

import java.util.Arrays;

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
    private int sourcePort;

    /**
     * The destination adress
     */
    private String destinationAddress;

    /**
     * The destination port
     */
    private int destinationPort;

    /**
     * The data of the datagram
     */
    private byte[] userData;

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public byte[] getUserData() {
        return Arrays.copyOf(userData, userData.length);
    }

    public void setUserData(byte[] userData) {
        this.userData = Arrays.copyOf(userData, userData.length);
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
