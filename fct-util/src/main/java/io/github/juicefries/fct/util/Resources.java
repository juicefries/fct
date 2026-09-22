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
// Data 2026/08/08 18:37
//

package io.github.juicefries.fct.util;

import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.sign.Uninitialized;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Logger;

public final class Resources implements Uninitialized {

    private final static Logger log = LoggerFactory.getLogger(Resources.class);
    private final static Lock lock = Lock.create();
    private final static AtomicBoolean IGNORE_ROLLBACK_WARNING = new AtomicBoolean(true);


    public static boolean isNotIgnoreRollbackWarning() {
        return IGNORE_ROLLBACK_WARNING.get();
    }

    public static InputStream getResourceAsStream(Class<?> clazz, String name) {
        if (clazz == null) {
            throw new NullPointerException("clazz is null!");
        }
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("The string is empty!");
        }

        try {
            return clazz.getResourceAsStream(name);
        } catch (Exception e) {
            if (isNotIgnoreRollbackWarning()) {
                log.warn("Failed to load from clazz.", e);
            }
        }

        try {
            //noinspection DataFlowIssue
            return clazz.getResource(name).openStream();
        } catch (IOException e) {
            if (isNotIgnoreRollbackWarning()) {
                log.warn("Failed to load from getResource.",e);
            }
        }

        try {
            return clazz.getClassLoader().getResourceAsStream(name);
        } catch (Exception e) {
            if (isNotIgnoreRollbackWarning()) {
                log.warn("Failed to load from ClassLoader.",e);
            }
        }

        try {
            return clazz.getModule().getResourceAsStream(name);
        } catch (IOException e) {
            if (isNotIgnoreRollbackWarning()) {
                log.warn("Failed to load from getModule.",e);
            }
        }

        if (isNotIgnoreRollbackWarning()) {
            log.error("Resource loading failed!");
        }
        return null;
    }

    public static URI getResourceURI(Class<?> clazz,String name,boolean create)
            throws NullPointerException,IllegalArgumentException,IOException
    {
        if (clazz == null) {
            throw new NullPointerException("clazz is null!");
        }
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("The string is empty!");
        }

        try {
            //noinspection DataFlowIssue
            return clazz.getResource(name).toURI();
        } catch (URISyntaxException e) {
            if (isNotIgnoreRollbackWarning()) {
                log.warn("Failed to try to get from [clazz]'s getResource(), attempting fallback.",e);
            }
        }

        try {
            return clazz.getClassLoader().getResource(name).toURI();
        } catch (URISyntaxException e) {
            if (isNotIgnoreRollbackWarning()) {
                log.warn("Failed to try to get from [clazz]'s"
                        + "getClassLoader() getResource(),"
                        + " attempting to fall back.",e
                );
            }
        }

        if (create) {
            return URI.create(name);
        }
        throw new IOException("Failed to get URI!");
    }

    public static URI getResourceURI(Class<?> clazz,String name) throws IOException {
        return getResourceURI(clazz,name,false);
    }


    private Resources() {

    }

}
