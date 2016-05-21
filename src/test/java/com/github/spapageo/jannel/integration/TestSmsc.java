/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Spyridon Papageorgiou
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

package com.github.spapageo.jannel.integration;

import org.jsmpp.PDUStringException;
import org.jsmpp.SMPPConstant;
import org.jsmpp.bean.*;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.*;
import org.jsmpp.util.MessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

class TestSmsc implements Runnable, ServerMessageReceiverListener  {

    @FunctionalInterface
    public interface SubmitSmProcessor {

        /**
         * Handles the submit sm that was received
         * @param submitSm the submit sm
         * @param source the smpp session
         * @return the message id of the operation
         * @throws ProcessRequestException in case of processing exception
         */
        MessageId onAcceptSubmitSm(SubmitSm submitSm,
                                   SMPPServerSession source) throws ProcessRequestException;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSmsc.class);

    private final ExecutorService execService = Executors.newFixedThreadPool(2);

    private final SubmitSmProcessor submitSmProcessor;

    private final AtomicBoolean running = new AtomicBoolean(true);

    private final int port;

    private final SMPPServerSessionListener sessionListener;

    TestSmsc(SubmitSmProcessor submitSmProcessor, int port) throws IOException {

        this.submitSmProcessor = submitSmProcessor;

        this.port = port;

        sessionListener = new SMPPServerSessionListener(port);

        execService.execute(this);
    }

    @Override
    public void run() {
        try {

            LOGGER.info("Listening on port {}", port);
            while (running.get()) {
                SMPPServerSession serverSession = sessionListener.accept();
                LOGGER.info("Accepting connection for session {}", serverSession.getSessionId());
                serverSession.setMessageReceiverListener(this);
                execService.execute(new WaitBindTask(serverSession));
            }
        } catch (IOException e) {
            LOGGER.error("IO error occurred", e);
        }
    }

    /**
     * Stop the server
     */
    void stop() throws IOException {
        sessionListener.close();
        this.running.set(false);
        this.execService.shutdownNow();
    }

    @Override
    public MessageId onAcceptSubmitSm(SubmitSm submitSm,
                                      SMPPServerSession source) throws ProcessRequestException {
        return submitSmProcessor.onAcceptSubmitSm(submitSm, source);
    }

    @Override
    public QuerySmResult onAcceptQuerySm(QuerySm querySm,
                                         SMPPServerSession source) throws ProcessRequestException {
        return null;
    }

    @Override
    public SubmitMultiResult onAcceptSubmitMulti(SubmitMulti submitMulti,
                                                 SMPPServerSession source) throws ProcessRequestException {
        return null;
    }

    @Override
    public DataSmResult onAcceptDataSm(DataSm dataSm, Session source)
            throws ProcessRequestException {
        return null;
    }

    @Override
    public void onAcceptCancelSm(CancelSm cancelSm, SMPPServerSession source)
            throws ProcessRequestException {
    }

    @Override
    public void onAcceptReplaceSm(ReplaceSm replaceSm, SMPPServerSession source)
            throws ProcessRequestException {
    }

    private class WaitBindTask implements Runnable {
        private final SMPPServerSession serverSession;

        WaitBindTask(SMPPServerSession serverSession) {
            this.serverSession = serverSession;
        }

        @Override
        public void run() {
            try {
                BindRequest bindRequest = serverSession.waitForBind(1000);
                LOGGER.debug("Accepting bind for session {}", serverSession.getSessionId());
                try {
                    bindRequest.accept("sys", InterfaceVersion.IF_34);
                } catch (PDUStringException e) {
                    LOGGER.error("Invalid system id", e);
                    bindRequest.reject(SMPPConstant.STAT_ESME_RSYSERR);
                }
            } catch (IllegalStateException e) {
                LOGGER.error("System error", e);
            } catch (TimeoutException e) {
                LOGGER.warn("Wait for bind has reach timeout", e);
            } catch (IOException e) {
                LOGGER.error("Failed accepting bind request for session {}", serverSession.getSessionId());
            }
        }
    }

}
