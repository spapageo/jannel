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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default session handler
 */
public class DefaultSessionHandler implements SessionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSessionHandler.class);

    private ClientSession clientSession;

    @Override
    public void fireSessionInitialized(ClientSession clientSession) {

        this.clientSession = clientSession;
    }

    @Override
    public void fireMessageExpired(Sms sms) {
        LOGGER.warn("Ignoring sms that has expired: {}", sms);
    }

    @Override
    public void fireBadMessageException(BadMessageException throwable) {
        LOGGER.warn("Ignoring BadMessageException that occurred: {}", throwable);
    }

    @Override
    public void fireUnknownThrowable(Throwable throwable) {
        LOGGER.warn("Ignoring unknown exception that occurred: {}", throwable);
    }

    @Override
    public void fireChannelUnexpectedlyClosed() {
        LOGGER.warn("Ignoring unexpected channel closure.");
    }

    @Override
    public void fireUnexpectedAckReceived(Ack ack) {
        LOGGER.warn("Ignoring unexpected ack that was received: {}", ack);
    }

    @Override
    public void fireSmsReceived(Sms sms) {
        Ack ack = new Ack(sms.getId());
        LOGGER.warn("Default response to a ignored sms is a successful acknowledgement: {}", sms);
        clientSession.sendAck(ack);
    }

    @Override
    public void fireAdminCommandReceived(Admin admin) {
        LOGGER.warn("Ignoring admin command that was received: {}", admin);
    }

    @Override
    public void fireHeartBeatReceived(HeartBeat heartBeat) {
        LOGGER.warn("Ignoring heart bean message: {}", heartBeat);
    }

    public ClientSession getClientSession() {
        return clientSession;
    }
}
