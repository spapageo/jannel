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

package com.github.spapageo.jannel.msg;

import com.github.spapageo.jannel.msg.enums.SmsConstants;

public class DlrMaskHelpers {
    /**
     * Undefined DLR mask
     */
    public static final int DLR_UNDEFINED = SmsConstants.PARAM_UNDEFINED;

    /**
     * DLR mask for nothing
     */
    public static final int DLR_NOTHING = 0x00;

    /**
     * DLR mask for only success reports
     */
    public static final int DLR_SUCCESS = 0x01;

    /**
     * DLR mask for only failure reports
     */
    public static final int DLR_FAIL = 0x02;

    /**
     * DLR mask for only buffered reports
     */
    public static final int DLR_BUFFERED = 0x04;

    /**
     * DLR mask for only SMSC success reports
     */
    public static final int DLR_SMSC_SUCCESS = 0x08;

    /**
     * DLR mask for only SMSC fail reports
     */
    public static final int DLR_SMSC_FAIL = 0x10;

    private DlrMaskHelpers() { }

    /**
     * @param dlr the dlr mask
     * @return whether this mask is defined
     */
    public static boolean isDefined(int dlr){
        return dlr != DLR_UNDEFINED;
    }

    /**
     * @param dlr the dlr mask
     * @return whether with mask has any DLR enabled
     */
    public static boolean isEnabled(int dlr) {
        return isDefined(dlr) && (dlr & (DLR_SUCCESS | DLR_FAIL | DLR_BUFFERED | DLR_SMSC_SUCCESS | DLR_SMSC_FAIL)) != DLR_NOTHING;
    }

    /**
     *
     * @param dlr the dlr mask
     * @return whether this mask corresponds to a DLR enabled device
     */
    public static boolean isEnabledDevice(int dlr){
        return isDefined(dlr) && (dlr & (DLR_SUCCESS | DLR_FAIL | DLR_BUFFERED)) != DLR_NOTHING;
    }

    /**
     * @param dlr the dlr mask
     * @return whether this mask corresponds to a DLR enabled SMSC
     */
    public static boolean isEnabledSmsc(int dlr) {
        return isDefined(dlr) && (dlr & (DLR_SMSC_SUCCESS | DLR_SMSC_FAIL)) != DLR_NOTHING;
    }

    /**
     * @param dlr the dlr mask
     * @return whether this mask has enabled intermediate DLR
     */
    public static boolean isNotFinal(int dlr) {
        return isDefined(dlr) && (dlr & (DLR_BUFFERED | DLR_SMSC_SUCCESS)) != DLR_NOTHING;
    }

    /**
     * @param dlr the dlr mask
     * @return whether this mask has enabled success or fail DLR
     */
    public static boolean isSuccessOrFail(int dlr){
        return isDefined(dlr) && (dlr & (DLR_SUCCESS | DLR_FAIL)) != DLR_NOTHING;
    }

    /**
     *
     * @param dlr the dlr mask
     * @return whether this mask has enabled success only
     */
    public static boolean isSuccess(int dlr){
        return isDefined(dlr) && (dlr & DLR_SUCCESS) != DLR_NOTHING;
    }

    /**
     * @param dlr the dlr mask
     * @return whether this mask has enabled failure only
     */
    public static boolean isFail(int dlr){
        return isDefined(dlr) && (dlr & DLR_FAIL) != DLR_NOTHING;
    }

    /**
     * @param dlr the dlr mask
     * @return whether this mask has enabled buffered only
     */
    public static boolean isBuffered(int dlr){
        return isDefined(dlr) && (dlr & DLR_BUFFERED) != DLR_NOTHING;
    }

    /**
     * @param dlr the dlr mask
     * @return whether this mask has enabled SMSC success only
     */
    public static boolean isSmscSuccess(int dlr){
        return isDefined(dlr) && (dlr & DLR_SMSC_SUCCESS) != DLR_NOTHING;
    }

    /**
     * @param dlr the dlr mask
     * @return whether this mask has enabled SMSC failure only
     */
    public static boolean isSmscFail(int dlr){
        return isDefined(dlr) && (dlr & DLR_SMSC_FAIL) != DLR_NOTHING;
    }
}
