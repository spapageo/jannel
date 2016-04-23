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

import org.junit.Test;

import static org.junit.Assert.*;
import static com.github.spapageo.jannel.msg.DlrMaskHelpers.*;

public class DlrMaskHelpersTest {

    @Test
    public void testIsDefined() throws Exception {
        int dlrMask = DLR_NOTHING;
        assertTrue(isDefined(dlrMask));

        dlrMask = DLR_UNDEFINED;
        assertFalse(isDefined(dlrMask));
    }

    @Test
    public void testIsEnabled() throws Exception {
        int dlrMask = DLR_UNDEFINED;
        assertFalse(isEnabled(dlrMask));

        dlrMask = DLR_NOTHING;
        assertFalse(isEnabled(dlrMask));

        dlrMask = DLR_SMSC_FAIL;
        assertTrue(isEnabled(dlrMask));
    }

    @Test
    public void testIsEnabledDevice() throws Exception {
        int dlrMask = DLR_UNDEFINED;
        assertFalse(isEnabledDevice(dlrMask));

        dlrMask = DLR_NOTHING;
        assertFalse(isEnabledDevice(dlrMask));

        dlrMask = DLR_SUCCESS;
        assertTrue(isEnabledDevice(dlrMask));
    }

    @Test
    public void testIsEnabledSmsc() throws Exception {
        int dlrMask = DLR_UNDEFINED;
        assertFalse(isEnabledSmsc(dlrMask));

        dlrMask = DLR_NOTHING;
        assertFalse(isEnabledSmsc(dlrMask));

        dlrMask = DLR_SMSC_SUCCESS;
        assertTrue(isEnabledSmsc(dlrMask));
    }

    @Test
    public void testIsNotFinal() throws Exception {
        int dlrMask = DLR_UNDEFINED;
        assertFalse(isNotFinal(dlrMask));

        dlrMask = DLR_NOTHING;
        assertFalse(isNotFinal(dlrMask));

        dlrMask = DLR_BUFFERED;
        assertTrue(isNotFinal(dlrMask));

        dlrMask = DLR_SMSC_SUCCESS;
        assertTrue(isNotFinal(dlrMask));
    }

    @Test
    public void testIsSuccessOrFail() throws Exception {
        int dlrMask = DLR_UNDEFINED;
        assertFalse(isSuccessOrFail(dlrMask));

        dlrMask = DLR_NOTHING;
        assertFalse(isSuccessOrFail(dlrMask));

        dlrMask = DLR_SUCCESS;
        assertTrue(isSuccessOrFail(dlrMask));

        dlrMask = DLR_FAIL;
        assertTrue(isSuccessOrFail(dlrMask));
    }

    @Test
    public void testIsSuccess() throws Exception {
        int dlrMask = DLR_UNDEFINED;
        assertFalse(isSuccess(dlrMask));

        dlrMask = DLR_NOTHING;
        assertFalse(isSuccess(dlrMask));

        dlrMask = DLR_SUCCESS;
        assertTrue(isSuccess(dlrMask));
    }

    @Test
    public void testIsFail() throws Exception {
        int dlrMask = DLR_UNDEFINED;
        assertFalse(isFail(dlrMask));

        dlrMask = DLR_NOTHING;
        assertFalse(isFail(dlrMask));

        dlrMask = DLR_FAIL;
        assertTrue(isFail(dlrMask));
    }

    @Test
    public void testIsBuffered() throws Exception {
        int dlrMask = DLR_UNDEFINED;
        assertFalse(isBuffered(dlrMask));

        dlrMask = DLR_NOTHING;
        assertFalse(isBuffered(dlrMask));

        dlrMask = DLR_BUFFERED;
        assertTrue(isBuffered(dlrMask));
    }

    @Test
    public void testIsSmscSuccess() throws Exception {
        int dlrMask = DLR_UNDEFINED;
        assertFalse(isSmscSuccess(dlrMask));

        dlrMask = DLR_NOTHING;
        assertFalse(isSmscSuccess(dlrMask));

        dlrMask = DLR_SMSC_SUCCESS;
        assertTrue(isSmscSuccess(dlrMask));
    }

    @Test
    public void testIsSmscFail() throws Exception {
        int dlrMask = DLR_UNDEFINED;
        assertFalse(isSmscFail(dlrMask));

        dlrMask = DLR_NOTHING;
        assertFalse(isSmscFail(dlrMask));

        dlrMask = DLR_SMSC_FAIL;
        assertTrue(isSmscFail(dlrMask));
    }

}