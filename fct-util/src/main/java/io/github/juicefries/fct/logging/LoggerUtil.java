/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 juicefries
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

//
// Created by juicefries
// The project name is fct
// Data 2026/09/24 16:47
//

package io.github.juicefries.fct.logging;

import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.util.Lock;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.ApiStatus;

public class LoggerUtil {

    private final static Lock lock = Lock.create();
    private final static AtomicBoolean notDefault = new AtomicBoolean(false);
    static LoggerContext context = new LoggerContext("FCT");

    @ApiStatus.Experimental
    @ApiSign.InternalApi
    public static void takeOver(LoggerContext context) {
        if (context == null) {
            throw new NullPointerException("context is null!");
        }

        synchronized (lock) {
            LoggerUtil.context = context;
            notDefault.set(true);
        }

    }

    public static LoggerContext getContext() {
        return context;
    }

    public static boolean isNotDefault() {
        return notDefault.get();
    }
}
