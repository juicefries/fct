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
// Data 2026/08/08 04:18
//

package io.github.juicefries.fct.util;

import io.github.juicefries.fct.sign.Readonly;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


public final class Lock implements Readonly {

    // nano time id
    private final long nt;
    private final long ctm;

    private Lock() {
        nt = System.nanoTime();
        ctm = System.currentTimeMillis();
    }

    public long getNt() {
        return nt;
    }

    public long getCtm() {
        return ctm;
    }

    /**
     * @return 无
     * @throws CloneNotSupportedException 该类不允许被克隆
     * @since 0.0.2
     * @deprecated 懒得写理由
     */
    @Override
    @Deprecated(since = "0.0.1", forRemoval = true)
    protected Lock clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("This class is not allowed to be cloned!");
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            throw new NullPointerException("object is null!");
        }

        if (!(object instanceof Lock lock)) return false;
        return (nt == lock.nt) && (ctm == lock.ctm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nt, ctm);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[nti=" + nt + ",ctm=" + ctm + "]";
    }

    @Contract(" -> new")
    public static @NotNull Lock create() {
        return new Lock();
    }

}
