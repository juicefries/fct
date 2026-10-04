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
// Data 2026/08/08 05:15
//

package io.github.juicefries.fct.logging;

import io.github.juicefries.fct.sign.Uninitialized;
import io.github.juicefries.fct.util.Lock;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.jetbrains.annotations.Contract;

@Deprecated(since = "1.0.4",forRemoval = true)
public class LoggerFactory implements Uninitialized {

    @Deprecated(since = "1.0.4",forRemoval = true)
    public final static String DEFAULT_LOG_CONFIG_FILE_PATH = "/io/github/juicefries/fct/logging/log4j2.xml";
    private static final Logger log = LogManager.getLogger(LoggerFactory.class);
    private final static Lock lock = Lock.create();
    private final static AtomicBoolean IGNORE_ROLLBACK_WARNING = new AtomicBoolean(true);

    @Contract(pure = true)
    private LoggerFactory() {

    }

    public static void setIgnoreRollbackWarning(boolean val) {
        synchronized (lock) {
            if (val == LoggerFactory.isNotIgnoreRollbackWarning()) {
                return;
            }
            IGNORE_ROLLBACK_WARNING.set(val);
        }
    }

    public static boolean isNotIgnoreRollbackWarning() {
        return IGNORE_ROLLBACK_WARNING.get();
    }

    @Deprecated(since = "1.0.4",forRemoval = true)
    public static Logger getLogger(String name) {
        outputWarning();
        return LogManager.getLogger(name);
    }

    @Deprecated(since = "1.0.4",forRemoval = true)
    public static Logger getLogger(String name, MessageFactory messageFactory) {
        outputWarning();
        return LogManager.getLogger(name, messageFactory);
    }

    @Deprecated(since = "1.0.4",forRemoval = true)
    public static Logger getLogger(Class<?> cls) {
        outputWarning();
        return LogManager.getLogger(cls);
    }

    @Deprecated(since = "1.0.4",forRemoval = true)
    public static Logger getLogger(Class<?> cls, MessageFactory messageFactory) {
        outputWarning();
        return LogManager.getLogger(cls,messageFactory);
    }

    private static void outputWarning() {
        if (isNotIgnoreRollbackWarning()) {
            log.warn("Since version 1.0.4, this class has been deprecated and the factory methods of org.apache.logging.log4j LogManager should be used, which have been redirected to the corresponding methods.");
        }
    }

}
