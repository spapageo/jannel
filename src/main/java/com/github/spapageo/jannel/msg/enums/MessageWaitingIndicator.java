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

package com.github.spapageo.jannel.msg.enums;

import javax.annotation.Nonnull;

/**
 * Flags that a message contains information about the some waiting message on a remote system
 */
public enum MessageWaitingIndicator {
    /**
     * Undefined
     */
    MWI_UNDEF(SmsConstants.PARAM_UNDEFINED),

    /**
     * Voice message waiting
     */
    MWI_VOICE_ON (0),

    /**
     * Fax message waiting
     */
    MWI_FAX_ON (1),

    /**
     * Email message waiting
     */
    MWI_EMAIL_ON(2),

    /**
     * Other type of message waiting
     */
    MWI_OTHER_ON(3),

    /**
     * Voice message waiting disabled
     */
    MWI_VOICE_OFF(4),

    /**
     * Fax message waiting disabled
     */
    MWI_FAX_OFF(5),

    /**
     * Email message waiting disabled
     */
    MWI_EMAIL_OFF(6),

    /**
     * Other type of message waiting disabled
     */
    MWI_OTHER_OFF(7);

    private static final MessageWaitingIndicator[] indexToValues = new MessageWaitingIndicator[8];

    static {
        indexToValues[MWI_VOICE_ON.value] = MWI_VOICE_ON;
        indexToValues[MWI_FAX_ON.value] = MWI_FAX_ON;
        indexToValues[MWI_EMAIL_ON.value] = MWI_EMAIL_ON;
        indexToValues[MWI_OTHER_ON.value] = MWI_OTHER_ON;
        indexToValues[MWI_VOICE_OFF.value] = MWI_VOICE_OFF;
        indexToValues[MWI_FAX_OFF.value] = MWI_FAX_OFF;
        indexToValues[MWI_EMAIL_OFF.value] = MWI_EMAIL_OFF;
        indexToValues[MWI_OTHER_OFF.value] = MWI_OTHER_OFF;
    }

    private final int value;

    MessageWaitingIndicator(int value) {
        this.value = value;
    }

    /**
     * Calculate the MessageWaitingIndicator by its value
     * @param value the value to convert to a MessageWaitingIndicator
     * @return the MessageWaitingIndicator
     */
    @Nonnull
    public static MessageWaitingIndicator fromValue(int value){
        return value > 7 || value < 0 ? MWI_UNDEF : indexToValues[value];
    }

    /**
     * @return the integer value
     */
    public int value() {
        return this.value;
    }
}
