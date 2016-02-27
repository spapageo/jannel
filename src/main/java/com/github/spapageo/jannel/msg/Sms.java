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

import java.util.UUID;

public class Sms implements Message{

    private String sender;

    private String receiver;

    private String udhData;

    private String msgData;

    private int time;

    private String smscId;

    private String smscNumber;

    private String foreignId;

    private String service;

    private String account;

    private UUID id;

    private SmsType smsType;

    private int mClass;

    private int mwi;

    private int coding;

    private int compress;

    private int validity;

    private int deferred;

    private int dlrMask;

    private String dlrUrl;

    private int pid;

    private int altDcs;

    private int rpi;

    private String charset;

    private String boxcId;

    private String binfo;

    private int msgLeft;

    private int priority;

    private int resendTry;

    private int resendTime;

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

    public String getUdhData() {
        return udhData;
    }

    public void setUdhData(String udhData) {
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

    public int getmClass() {
        return mClass;
    }

    public void setmClass(int mClass) {
        this.mClass = mClass;
    }

    public int getMwi() {
        return mwi;
    }

    public void setMwi(int mwi) {
        this.mwi = mwi;
    }

    public int getCoding() {
        return coding;
    }

    public void setCoding(int coding) {
        this.coding = coding;
    }

    public int getCompress() {
        return compress;
    }

    public void setCompress(int compress) {
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

    public int getRpi() {
        return rpi;
    }

    public void setRpi(int rpi) {
        this.rpi = rpi;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getBoxcId() {
        return boxcId;
    }

    public void setBoxcId(String boxcId) {
        this.boxcId = boxcId;
    }

    public String getBinfo() {
        return binfo;
    }

    public void setBinfo(String binfo) {
        this.binfo = binfo;
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
               ", mClass=" + mClass +
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
               ", boxcId='" + boxcId + '\'' +
               ", binfo='" + binfo + '\'' +
               ", msgLeft=" + msgLeft +
               ", priority=" + priority +
               ", resendTry=" + resendTry +
               ", resendTime=" + resendTime +
               ", metaData='" + metaData + '\'' +
               '}';
    }
}
