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

/**
 * The message class
 */
public enum MessageClass {
    /**
     * Undefined
     */
    MC_UNDEF(SmsConstants.PARAM_UNDEFINED),

    /**
     * Forward to display
     */
    MC_CLASS0(0),

    /**
     * Forward to mobile
     */
    MC_CLASS1(1),

    /**
     * Forward to SIM
     */
    MC_CLASS2(2),

    /**
     * Forward to SIM Toolkit
     */
    MC_CLASS3(3);

    private static final MessageClass[] indexToValues = new MessageClass[4];

    static {
        indexToValues[MC_CLASS0.value] = MC_CLASS0;
        indexToValues[MC_CLASS1.value] = MC_CLASS1;
        indexToValues[MC_CLASS2.value] = MC_CLASS2;
        indexToValues[MC_CLASS3.value] = MC_CLASS3;
    }

    private final int value;

    MessageClass(int value) {
        this.value = value;
    }

    /**
     * Calculate the MessageClass by its value
     * @param value the value to convert to a MessageClass
     * @return the MessageClass
     */
    public static MessageClass fromValue(int value){
        return value > 3 || value < 0 ? MC_UNDEF : indexToValues[value];
    }

    /**
     * @return the integer value
     */
    public int value() {
        return this.value;
    }
}
