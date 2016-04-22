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

import com.github.spapageo.jannel.Integration;
import com.github.spapageo.jannel.client.ClientSession;
import com.github.spapageo.jannel.client.ClientSessionConfiguration;
import com.github.spapageo.jannel.client.JannelClient;
import com.github.spapageo.jannel.msg.Sms;
import com.github.spapageo.jannel.msg.SmsType;
import com.github.spapageo.jannel.msg.enums.DataCoding;
import com.github.spapageo.jannel.transcode.TestSmsc;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jsmpp.util.MessageIDGenerator;
import org.jsmpp.util.RandomMessageIDGenerator;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ThroughputTest {

    private ClientSessionConfiguration configuration;

    private JannelClient jannelClient = new JannelClient(new NioEventLoopGroup(1),
                                                         NioSocketChannel.class,
                                                         new NioEventLoopGroup(1));

    private MessageIDGenerator generator = new RandomMessageIDGenerator();

    @Before
    public void setup(){
        configuration = new ClientSessionConfiguration("javaClient");
        configuration.setHost(Integration.KANNEL_HOST);
        configuration.setPort(Integration.KANNEL_PORT);
        configuration.setRequestExpiryTimeout(50000);
        configuration.setWindowMonitorInterval(25000);
        configuration.setWindowSize(5000);
    }

    @Test
    public void testThatTheClientCanDeliverFiftyThousandMessages() throws Exception {
        ClientSession clientSession = jannelClient.identify(configuration, null);
        CountDownLatch latch = new CountDownLatch(50000);

        TestSmsc testSmsc = new TestSmsc((submitSm, source) -> {
            assertEquals("Hello World ασδασδ ςαδ`", new String(submitSm.getShortMessage(), StandardCharsets.UTF_16BE));
            assertEquals("hello", submitSm.getSourceAddr());
            assertEquals("306975834115", submitSm.getDestAddress());
            assertEquals(8, submitSm.getDataCoding());

            latch.countDown();
            return generator.newMessageId();
        }, 7777);

        for (int i = 0; i < 50000; i++){
            Sms sms = new Sms("hello",
                              "306975834115",
                              "Hello World ασδασδ ςαδ`",
                              SmsType.MOBILE_TERMINATED_PUSH,
                              DataCoding.DC_UCS2);
            clientSession.sendSmsAndWait(sms, 5000);
        }

        assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
        testSmsc.stop();
    }
}
