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
 * Flags that a message is compressed
 */
public enum Compress {
    /**
     * Undefined
     */
    COMPRESS_UNDEF(SmsConstants.PARAM_UNDEFINED),

    /**
     * Flags that the message body is not compressed
     */
    COMPRESS_OFF(0),

    /**
     * Flags that the message body is compressed
     */
    COMPRESS_ON(1);

    private static final Compress[] indexToValues = new Compress[2];

    static {
        indexToValues[COMPRESS_OFF.value] = COMPRESS_OFF;
        indexToValues[COMPRESS_ON.value] = COMPRESS_ON;
    }

    private final int value;

    Compress(int value) {
        this.value = value;
    }

    /**
     * Calculate the Compress by its value
     * @param value the value to convert to a Compress
     * @return the Compress
     */
    @Nonnull
    public static Compress fromValue(int value){
        return value > 1 || value < 0 ? COMPRESS_UNDEF : indexToValues[value];
    }

    /**
     * @return the integer value
     */
    public int value() {
        return this.value;
    }
}
