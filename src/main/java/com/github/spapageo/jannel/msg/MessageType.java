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

/**
 * The message type used to communicate between the bearer-box and the client box
 */
public enum MessageType {

    /**
     * A verification that the other end in still alive
     */
    HEARTBEAT(0),

    /**
     * Administration commands
     */
    ADMIN(1),

    /**
     * A short message
     */
    SMS(2),

    /**
     * Acknowledgement of message reception
     */
    ACK(3),

    /**
     * A data carrying message
     */
    DATAGRAM(4),

    /**
     * Unknown message type
     */
    UNKNOWN(-1);

    private final int value;

    private static final MessageType[] valueMap = new MessageType[6];

    static {
        valueMap[HEARTBEAT.value] = HEARTBEAT;
        valueMap[ADMIN.value] = ADMIN;
        valueMap[SMS.value] = SMS;
        valueMap[ACK.value] = ACK;
        valueMap[DATAGRAM.value] = DATAGRAM;
    }

    MessageType(int value) {
        this.value = value;
    }

    public int value(){
        return value;
    }

    public static MessageType fromValue(int value){
        return value < 0 || value > 4 ? UNKNOWN : valueMap[value];
    }
}
