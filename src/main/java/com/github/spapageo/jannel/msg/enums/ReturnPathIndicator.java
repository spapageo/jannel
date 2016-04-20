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
 * Flags a message that contains a return path indicator in its header
 */
public enum ReturnPathIndicator {

    /**
     * Undefined
     */
    RPI_UNDEF(SmsConstants.PARAM_UNDEFINED),

    /**
     * Flags that the return path indicator is not included
     */
    RPI_OFF(0),

    /**
     * Flags that the return path indicator is included in the user data header
     */
    RPI_ON(1);

    private static final ReturnPathIndicator[] indexToValues = new ReturnPathIndicator[2];

    static {
        indexToValues[RPI_OFF.value] = RPI_OFF;
        indexToValues[RPI_ON.value] = RPI_ON;
    }

    private final int value;

    ReturnPathIndicator(int value) {
        this.value = value;
    }

    /**
     * Calculate the ReturnPathIndicator by its value
     * @param value the value to convert to a ReturnPathIndicator
     * @return the ReturnPathIndicator
     */
    public static ReturnPathIndicator fromValue(int value){
        return value > 1 || value < 0 ? RPI_UNDEF : indexToValues[value];
    }

    /**
     * @return the integer value
     */
    public int value() {
        return this.value;
    }
}
