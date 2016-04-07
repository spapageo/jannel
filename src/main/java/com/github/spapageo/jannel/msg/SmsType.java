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
 * The possible sms types
 */
public enum SmsType {
    /**
     * Message the originated from a modile
     */
    MOBILE_ORIENTED(0),

    /**
     * Message the terminated at a mobile and is a reply
     */
    MOBILE_TERMINATED_REPLY(1),

    /**
     * Message the terminated at a mobile and is a push message
     */
    MOBILE_TERMINATED_PUSH(2),

    /**
     * Mobile oriented report message
     */
    REPORT_MOBILE_ORIENTED(3),

    /**
     * Mobile terminated report message
     */
    REPORT_MOBILE_TERMINATED(4),

    /**
     * Not defined
     */
    SMS_UNDEF(-1);

    private final int value;

    private static final SmsType[] valueMap = new SmsType[5];

    static {
        valueMap[MOBILE_ORIENTED.value] = MOBILE_ORIENTED;
        valueMap[MOBILE_TERMINATED_REPLY.value] = MOBILE_TERMINATED_REPLY;
        valueMap[MOBILE_TERMINATED_PUSH.value] = MOBILE_TERMINATED_PUSH;
        valueMap[REPORT_MOBILE_ORIENTED.value] = REPORT_MOBILE_ORIENTED;
        valueMap[REPORT_MOBILE_TERMINATED.value] = REPORT_MOBILE_TERMINATED;
    }

    SmsType(int value) {
        this.value = value;
    }

    public int value(){
        return value;
    }

    /**
     * Calculate the SmsType by its value
     * @param value the value to convert to a SmsType
     * @return the SmsType
     */
    public static SmsType fromValue(int value){
        return value < 0 || value > 4 ? SMS_UNDEF : valueMap[value];
    }

}
