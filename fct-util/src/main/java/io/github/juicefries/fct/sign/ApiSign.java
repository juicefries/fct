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
// Data 2026/08/22 22:45
//

package io.github.juicefries.fct.sign;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.ApiStatus;

/**
 * API标记
 * <p>
 *     用于标记API。
 * </p>
 * @since 0.0.3
 * @author juicefries
 */
public final class ApiSign {

    /**
     * 表示标记接口，也就是空接口。
     */
    @Documented
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.TYPE)
    public @interface SignInterface {
        String since() default "";
    }

    /**
     * 危险的
     * <p>
     *     表示对应的API 类 或包是危险的不建议使用。
     * </p>
     */
    @Documented
    @Retention(RetentionPolicy.CLASS)
    @Target({
            ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE
    })
    public @interface Dangerous {

        String since() default "";

        boolean forRemoval() default false;

    }

    /**
     * 不推荐的
     * <p>
     *     表示对应的不推荐。
     * </p>
     */
    @Documented
    @Retention(RetentionPolicy.CLASS)
    @Target({
            ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE
    })
    public @interface NotRecommended {

        String since() default "";

        boolean forRemoval() default false;

    }

    /**
     * 表示为内部API，非常规方法
     */
    @Documented
    @Retention(RetentionPolicy.CLASS)
    @Target({
            ElementType.TYPE,
            ElementType.ANNOTATION_TYPE,
            ElementType.METHOD,
    })
    public @interface InternalApi {
        String since() default "";
    }

}
