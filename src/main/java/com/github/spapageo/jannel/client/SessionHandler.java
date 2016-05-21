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

package com.github.spapageo.jannel.client;

import com.github.spapageo.jannel.exception.BadMessageException;
import com.github.spapageo.jannel.msg.Ack;
import com.github.spapageo.jannel.msg.Admin;
import com.github.spapageo.jannel.msg.HeartBeat;
import com.github.spapageo.jannel.msg.Sms;

/**
 * Interface that user of the session that a specific event occurred
 */
public interface SessionHandler {

    /**
     * Fired when the session is initialized after identifying to the remote bearer-box
     * @param clientSession the new session
     */
    void fireSessionInitialized(ClientSession clientSession);

    /**
     * Fired when the specified sms expired
     * @param sms the sent sms that has expired because no response has appered in time
     */
    void fireMessageExpired(Sms sms);

    /**
     * Fired when a bad message could not be de-serialized
     * @param throwable the bad message exception
     */
    void fireBadMessageException(BadMessageException throwable);

    /**
     * Fired when an unknown exception has occurred during the read/write of a message
     * @param throwable the exception that occurred
     */
    void fireUnknownThrowable(Throwable throwable);

    /**
     * Fired when the channel was closed without the explicit user request
     */
    void fireChannelUnexpectedlyClosed();

    /**
     * Fired when an unexpected ack was received
     * @param ack the ack message
     */
    void fireUnexpectedAckReceived(Ack ack);

    /**
     * Fired when an Sms is received
     * @param sms the sms that was received
     */
    void fireSmsReceived(Sms sms);

    /**
     * Fired when an admin command was received
     * @param admin the admin command
     */
    void fireAdminCommandReceived(Admin admin);

    /**
     * Fired when a heartbeat is received
     * @param heartBeat the heart beat
     */
    void fireHeartBeatReceived(HeartBeat heartBeat);
}
