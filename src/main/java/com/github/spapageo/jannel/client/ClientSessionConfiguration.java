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

/**
 * Configuration used for the session and client connection to the bearer-box
 */
public class ClientSessionConfiguration {

    public static final int DEFAULT_WINDOW_SIZE = 1;

    public static final long DEFAULT_WRITE_TIMEOUT = 0;

    public static final long DEFAULT_CONNECT_TIMEOUT = 10000;

    public static final long DEFAULT_REQUEST_EXPIRY_TIMEOUT = -1;

    public static final String DEFAULT_CLIENT_NAME = "jannel_client";

    private String host;

    private int port;

    private long connectTimeout;

    private int windowSize;

    private String clientId;

    private long requestExpiryTimeout;

    private long writeTimeout;

    public ClientSessionConfiguration() {
        this(DEFAULT_CLIENT_NAME);
    }

    public ClientSessionConfiguration(String clientId) {
        this.clientId = clientId;
        this.windowSize = DEFAULT_WINDOW_SIZE;
        this.requestExpiryTimeout = DEFAULT_REQUEST_EXPIRY_TIMEOUT;
        this.writeTimeout = DEFAULT_WRITE_TIMEOUT;
        this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    }

    public void setWindowSize(int value) {
        this.windowSize = value;
    }

    public int getWindowSize() {
        return this.windowSize;
    }

    /**
     * Set the amount of time to wait for an endpoint to respond to
     * a request before it expires. Defaults to disabled (-1).
     * @param requestExpiryTimeout  The amount of time to wait (in ms) before
     *      an unacknowledged request expires.  -1 disables.
     */
    public void setRequestExpiryTimeout(long requestExpiryTimeout) {
        this.requestExpiryTimeout = requestExpiryTimeout;
    }


    public long getRequestExpiryTimeout() {
        return requestExpiryTimeout;
    }

    /**
     * Sets the write timeout for outgoing messages in milliseconds
     * @param writeTimeout the new write timeout
     */
    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    /**
     * @return the write timeout in milliseconds
     */
    public long getWriteTimeout() {
        return writeTimeout;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

}
