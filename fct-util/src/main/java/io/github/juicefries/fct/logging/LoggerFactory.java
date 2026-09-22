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
import io.github.juicefries.fct.util.Resources;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.message.MessageFactory;

public class LoggerFactory implements Uninitialized {

    public final static String DEFAULT_LOG_CONFIG_FILE_PATH = "/io/github/juicefries/fct/logging/log4j2.xml";

    private final static Lock lock = Lock.create();
    private final static LoggerContext context = new LoggerContext("FCT");
    private final static Logger log = LoggerFactory.getLogger(LoggerFactory.class);

    private static volatile boolean Init = false;
    private static Level level = Level.INFO;
    private static String logConfigFilePath = DEFAULT_LOG_CONFIG_FILE_PATH;

    static {
        if (!Init) {
            upConfig();
            Init = true;
        }
    }

    public static void upConfig() {
        if (!Init) {
            synchronized (lock) {
                System.setProperty("fct.log.out.level", level.toString());
            }
        }
        configURI();
        synchronized (lock) {
            Configuration configuration = context.getConfiguration();
            LoggerConfig config = configuration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
            if (!Init) {
                config.setLevel(level);
            } else {
                if (config.getLevel() != level) {
                    config.setLevel(level);
                }
            }
            context.updateLoggers();
        }
        if (!Init) {
            Init = true;
        }
    }

    private static void configURI() {
        if (Init) {
            if (Objects.equals(logConfigFilePath, context.getConfigLocation().getPath())) {
                return;
            }
        }
        try {
            synchronized (lock) {
                URI uri = Resources.getResourceURI(LoggerFactory.class, logConfigFilePath);
                context.setConfigLocation(uri);
            }
        } catch (IOException e) {
            log.warn("Log configuration failed to load!",e);
        }
    }


    private LoggerFactory() {

    }

    public static void setLevel(Level level) {
        if (level == null) {
            throw new NullPointerException("level is null!");
        }
        LoggerFactory.level = level;
        upConfig();
    }

    public static void setLogConfigFilePath(String path) {
        if (path == null) {
            throw new NullPointerException("path is null!");
        }
        logConfigFilePath = path;
        upConfig();
    }

    public static Logger getLogger(String name) {
        return context.getLogger(name);
    }

    public static Logger getLogger(String name, MessageFactory messageFactory) {
        return context.getLogger(name, messageFactory);
    }

    public static Logger getLogger(Class<?> cls) {
        return context.getLogger(cls);
    }

    public static Logger getLogger(Class<?> cls, MessageFactory messageFactory) {
        return context.getLogger(cls,messageFactory);
    }

    public static Level getLevel() {
        return level;
    }

    public static String getLogConfigFilePath() {
        return logConfigFilePath;
    }

    public static boolean isInit() {
        return Init;
    }

}
