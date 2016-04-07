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

import com.github.spapageo.jannel.msg.enums.*;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

/**
 * An sms message
 */
public class Sms implements Message{

    /**
     * The sms sender
     */
    private String sender;

    /**
     * The sms receiver
     */
    private String receiver;

    /**
     * User data header
     */
    private ByteBuf udhData;

    /**
     * The message data
     */
    //TODO Must respect the charset field
    private String msgData;

    /**
     * The sms timestamp
     */
    private int time;

    /**
     * The smsc id to be used for routing the sms
     */
    private String smscId;

    private String smscNumber;

    /**
     * The sms id used by a foreign system
     */
    private String foreignId;

    /**
     * The sms service
     */
    private String service;

    /**
     * The account associated with the sms
     */
    private String account;

    /**
     * The sms id
     */
    private UUID id;

    /**
     * The sms type
     */
    private SmsType smsType;

    /**
     * Message class
     */
    private MessageClass messageClass;

    /**
     * Message waiting indicator
     */
    private MessageWaitingIndicator mwi;

    /**
     * Message coding
     */
    private DataCoding coding;

    /**
     * Message compression
     */
    private Compress compress;

    /**
     * The validity of the sms in minutes
     */
    private int validity;

    /**
     * The minutes to pass before the bearer-box forwards the message
     */
    private int deferred;

    /**
     * The bit mask of the dlr messages that should be enables for this sms
     */
    private int dlrMask;

    /**
     * The url to fetch when a new dlr is received for message
     */
    private String dlrUrl;

    /**
     * Protocol id
     */
    private int pid;

    /**
     * Alternative data coding scheme
     */
    private int altDcs;

    /**
     * Return path indicator
     */
    private ReturnPathIndicator rpi;

    /**
     * The charset the that message data is encoded in.
     * This value should be almost always UTF-8 as it affects the
     * message byte representation
     */
    private String charset;

    /**
     * The id of the box that sent this message
     */
    private String boxId;

    /**
     * The billing info of this message
     */
    private String billingInfo;

    /**
     * The number of remaining messages when this message is part of a whole
     */
    private int msgLeft;

    /**
     * The message priority
     */
    private int priority;

    /**
     * The current resend retry
     */
    private int resendTry;

    /**
     * The next resend time
     */
    private int resendTime;

    /**
     * The message metadata
     */
    private String metaData;

    @Override
    public MessageType getType() {
        return MessageType.SMS;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public ByteBuf getUdhData() {
        return udhData;
    }

    public void setUdhData(ByteBuf udhData) {
        this.udhData = udhData;
    }

    public String getMsgData() {
        return msgData;
    }

    public void setMsgData(String msgData) {
        this.msgData = msgData;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getSmscId() {
        return smscId;
    }

    public void setSmscId(String smscId) {
        this.smscId = smscId;
    }

    public String getSmscNumber() {
        return smscNumber;
    }

    public void setSmscNumber(String smscNumber) {
        this.smscNumber = smscNumber;
    }

    public String getForeignId() {
        return foreignId;
    }

    public void setForeignId(String foreignId) {
        this.foreignId = foreignId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public SmsType getSmsType() {
        return smsType;
    }

    public void setSmsType(SmsType smsType) {
        this.smsType = smsType;
    }

    public MessageClass getMessageClass() {
        return messageClass;
    }

    public void setMessageClass(MessageClass messageClass) {
        this.messageClass = messageClass;
    }

    public MessageWaitingIndicator getMwi() {
        return mwi;
    }

    public void setMwi(MessageWaitingIndicator mwi) {
        this.mwi = mwi;
    }

    public DataCoding getCoding() {
        return coding;
    }

    public void setCoding(DataCoding coding) {
        this.coding = coding;
    }

    public Compress getCompress() {
        return compress;
    }

    public void setCompress(Compress compress) {
        this.compress = compress;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public int getDeferred() {
        return deferred;
    }

    public void setDeferred(int deferred) {
        this.deferred = deferred;
    }

    public int getDlrMask() {
        return dlrMask;
    }

    public void setDlrMask(int dlrMask) {
        this.dlrMask = dlrMask;
    }

    public String getDlrUrl() {
        return dlrUrl;
    }

    public void setDlrUrl(String dlrUrl) {
        this.dlrUrl = dlrUrl;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getAltDcs() {
        return altDcs;
    }

    public void setAltDcs(int altDcs) {
        this.altDcs = altDcs;
    }

    public ReturnPathIndicator getRpi() {
        return rpi;
    }

    public void setRpi(ReturnPathIndicator rpi) {
        this.rpi = rpi;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getBillingInfo() {
        return billingInfo;
    }

    public void setBillingInfo(String billingInfo) {
        this.billingInfo = billingInfo;
    }

    public int getMsgLeft() {
        return msgLeft;
    }

    public void setMsgLeft(int msgLeft) {
        this.msgLeft = msgLeft;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getResendTry() {
        return resendTry;
    }

    public void setResendTry(int resendTry) {
        this.resendTry = resendTry;
    }

    public int getResendTime() {
        return resendTime;
    }

    public void setResendTime(int resendTime) {
        this.resendTime = resendTime;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    @Override
    public String toString() {
        return "Sms{" +
               "id=" + id +
               ", sender='" + sender + '\'' +
               ", receiver='" + receiver + '\'' +
               ", udhData='" + udhData + '\'' +
               ", msgData='" + msgData + '\'' +
               ", time=" + time +
               ", smscId='" + smscId + '\'' +
               ", smscNumber='" + smscNumber + '\'' +
               ", foreignId='" + foreignId + '\'' +
               ", service='" + service + '\'' +
               ", account='" + account + '\'' +
               ", smsType=" + smsType +
               ", messageClass=" + messageClass +
               ", mwi=" + mwi +
               ", coding=" + coding +
               ", compress=" + compress +
               ", validity=" + validity +
               ", deferred=" + deferred +
               ", dlrMask=" + dlrMask +
               ", dlrUrl='" + dlrUrl + '\'' +
               ", pid=" + pid +
               ", altDcs=" + altDcs +
               ", rpi=" + rpi +
               ", charset='" + charset + '\'' +
               ", boxId='" + boxId + '\'' +
               ", billingInfo='" + billingInfo + '\'' +
               ", msgLeft=" + msgLeft +
               ", priority=" + priority +
               ", resendTry=" + resendTry +
               ", resendTime=" + resendTime +
               ", metaData='" + metaData + '\'' +
               '}';
    }
}
