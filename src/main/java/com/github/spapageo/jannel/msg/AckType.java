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

import java.util.HashMap;
import java.util.Map;

/**
 * The types of acknowledgement
 */
public enum AckType {

    /**
     * Success
     */
    SUCCESS(0),

    /**
     *  Do not try again (e.g. no route)
     */
    FAILED(1),

    /**
     * temporary failed, try again (e.g. queue full)
     */
    FAILED_TMP(2),

    /**
     * Buffered response
     */
    BUFFERED(3),

    /**
     * Unknown repsonse
     */
    UNKNOWN(-1);

    private final int value;

    private static final Map<Integer,AckType> valueMap = new HashMap<>();

    static {
        valueMap.put(0, SUCCESS);
        valueMap.put(1, FAILED);
        valueMap.put(2, FAILED_TMP);
        valueMap.put(3, BUFFERED);
    }

    AckType(int value) {
        this.value = value;
    }

    public int value(){
        return value;
    }

    /**
     * Calculate the AckType by its value
     * @param value the value to convert to a AckType
     * @return the AckType
     */
    public static AckType fromValue(int value){
        return valueMap.getOrDefault(value, UNKNOWN);
    }
}
