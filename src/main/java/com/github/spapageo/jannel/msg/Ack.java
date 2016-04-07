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

import java.util.UUID;

/**
 * An acknowledgement to a message
 */
public class Ack implements Message{

    /**
     * The id of the acknowledged message
     */
    private UUID id;

    /**
     * The response type
     */
    private AckType response;

    /**
     * The response timestamp
     */
    private int time;

    /**
     * Construct a new ack message
     * @param id the ack id
     * @param response the response
     * @param time the timestamp of the response
     */
    public Ack(UUID id, AckType response, int time) {
        this.id = id;
        this.response = response;
        this.time = time;
    }

    /**
     * Construct a new ack with the given id, a success response and the current system time
     * @param id the ack id
     */
    public Ack(UUID id) {
        this(id, AckType.SUCCESS, (int) System.currentTimeMillis());
    }

    /**
     * Default construction
     */
    public Ack() {
        this.time = SmsConstants.PARAM_UNDEFINED;
        this.response = AckType.ACK_UNDEF;
    }

    @Override
    public MessageType getType() {
        return MessageType.ACK;
    }

    /**
     * @return the ack id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the ack id
     * @param id the new id
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the ack response
     */
    public AckType getResponse() {
        return response;
    }

    /**
     * Sets the ack type
     * @param response the new response
     */
    public void setResponse(AckType response) {
        this.response = response;
    }

    /**
     * @return the ack time
     */
    public int getTime() {
        return time;
    }

    /**
     * Sets the ack time
     * @param time the new time
     */
    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Ack{" +
                "id=" + id +
                ", response=" + response +
                ", time=" + time +
                '}';
    }
}
