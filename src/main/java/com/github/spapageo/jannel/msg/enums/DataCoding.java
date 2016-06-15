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
 * The message encoding
 */
public enum DataCoding {
    /**
     * Undefined
     */
    DC_UNDEF(SmsConstants.PARAM_UNDEFINED),

    /**
     * Flags that the message body should be encoded in 7-bit gsm
     */
    DC_7BIT(0),

    /**
     * Flags that the message body should be encoded in 8-bit gsm, mainly because it has a user data
     * header
     */
    DC_8BIT(1),

    /**
     * Flags that the message body should be encoded in UTF-8, mainly because the message data is
     * unicode text
     */
    DC_UCS2(2);

    private static final DataCoding[] indexToValues = new DataCoding[3];

    static {
        indexToValues[DC_7BIT.value] = DC_7BIT;
        indexToValues[DC_8BIT.value] = DC_8BIT;
        indexToValues[DC_UCS2.value] = DC_UCS2;
    }

    private final int value;

    DataCoding(int value) {
        this.value = value;
    }

    /**
     * Calculate the DataCoding by its value
     * @param value the value to convert to a DataCoding
     * @return the DataCoding
     */
    @Nonnull
    public static DataCoding fromValue(int value){
        return value > 2 || value < 0 ? DC_UNDEF : indexToValues[value];
    }

    /**
     * @return the integer value
     */
    public int value() {
        return this.value;
    }
}
