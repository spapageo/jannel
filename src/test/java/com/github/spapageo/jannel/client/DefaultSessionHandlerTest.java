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

import com.github.spapageo.jannel.exception.StringSizeException;
import com.github.spapageo.jannel.msg.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class DefaultSessionHandlerTest {

    DefaultSessionHandler handler;

    ClientSession clientSession;

    @Before
    public void setup(){
        handler = new DefaultSessionHandler();
        clientSession = mock(ClientSession.class);
        handler.fireSessionInitialized(clientSession);
    }

    @Test
    public void testFireSessionInitialized() throws Exception {
        handler = new DefaultSessionHandler();
        handler.fireSessionInitialized(clientSession);
        assertSame(clientSession, handler.getClientSession());
    }

    @Test
    public void testFireMessageExpired() throws Exception {
        handler.fireMessageExpired(mock(Sms.class));
        verifyNoMoreInteractions(clientSession);
    }

    @Test
    public void testFireBadMessageException() throws Exception {
        handler.fireBadMessageException(new StringSizeException(""));
        verifyNoMoreInteractions(clientSession);
    }

    @Test
    public void testFireUnknownThrowable() throws Exception {
        handler.fireUnknownThrowable(new StringSizeException(""));
        verifyNoMoreInteractions(clientSession);
    }

    @Test
    public void testFireChannelUnexpectedlyClosed() throws Exception {
        handler.fireChannelUnexpectedlyClosed();
        verifyNoMoreInteractions(clientSession);
    }

    @Test
    public void testFireUnexpectedAckReceived() throws Exception {
        handler.fireUnexpectedAckReceived(mock(Ack.class));
        verifyNoMoreInteractions(clientSession);
    }

    @Test
    public void testFireSmsReceived() throws Exception {

        Sms sms = new Sms();
        sms.setId(UUID.randomUUID());

        handler.fireSmsReceived(sms);

        ArgumentCaptor<Ack> captor = ArgumentCaptor.forClass(Ack.class);

        verify(clientSession).sendAck(captor.capture());

        Assert.assertEquals(AckType.SUCCESS, captor.getValue().getResponse());
        assertEquals(sms.getId(), captor.getValue().getId());
    }

    @Test
    public void testFireAdminCommandReceived() throws Exception {
        handler.fireAdminCommandReceived(mock(Admin.class));
        verifyNoMoreInteractions(clientSession);
    }

    @Test
    public void testFireHeartBeatReceived() throws Exception {
        handler.fireHeartBeatReceived(mock(HeartBeat.class));
        verifyNoMoreInteractions(clientSession);
    }
}