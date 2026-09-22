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

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.sign.ApiSign;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.Callback;

/**
 * 不要实现的窗口处理者接口
 *
 * <p>
 *     如果你想使用这些难绷的工具方法，
 *     <br>
 *     大可以在实现{@link WindowHandler}的情况下实现它
 * </p>
 * @since 0.0.3
 * @see WindowHandler
 * @author juicefries
 */
@ApiStatus.Experimental
@ApiSign.Dangerous(since = "0.0.3",forRemoval = true)
public interface UnqualifiedWindowHandlerI extends WindowHandler {

    @ApiStatus.Experimental
    default <T> T check(Callback callback, Class<? extends T> clazz) {
        if (callback == null) return null;
        else if (!getUpdateData().isKeep()) return null;
        else if (callback == this) return null;
        else if (clazz.isInstance(callback)) {
            //noinspection unchecked
            return (T) callback;
        } else {
            return null;
        }
    }

    @ApiStatus.Experimental
    default <T> void invoke(Callback callback, Class<? extends T> clazz, Consumer<T> consumer) {
        T t = check(callback,clazz);
        if (t == null) return;
        consumer.accept(t);
    }


    /**
     * 在判断中取反以此减少嵌套
     * @param callback 回调
     * @param clazz 类引用
     * @return 结果
     * @param <T> 类型
     */
    default <T extends Callback> boolean checkB(Callback callback, Class<? extends T> clazz) {
        if (callback == null) return true;
        else if (!getUpdateData().isKeep()) return true;
        else if (callback == this) return true;
        else return !clazz.isInstance(callback);
    }

    /*
     * 令我难绷的逆天方法，我觉得很太神经所以保留，并准备写个更难绷的
     * data-2026-8-24|02:30
     */
    @ApiStatus.Experimental
    default void glfwCallbacksCheckAndForList(Consumer<Callback> callback) {
        if (callback == null) throw new NullPointerException("callback is null!");

        if (!getUpdateData().isKeep()) return;
        if (getUpdateData().getGlfwCallbacks().isEmpty()) return;
        getUpdateData().getGlfwCallbacks().forEach(callback);
    }

    @ApiStatus.Experimental
    default <T extends Callback> void glfwCallbacksCheckAndForList(
            Class<? extends T> clazz,Consumer<T> consumer
    ) {
        if (clazz == null) throw new NullPointerException("clazz is null!");
        if (consumer == null) throw new NullPointerException("consumer is null!");

        glfwCallbacksCheckAndForList(callback -> invoke(callback,clazz,consumer));
    }

    @ApiStatus.Experimental
    default <T extends Callback> void forGlfwList(
            Class<? extends T> clazz,Consumer<T> consumer
    ) {
        glfwCallbacksCheckAndForList(clazz,consumer);
    }

    default <C extends Callback> void traverseAndCheckTheGLFWCallbackList(
            Class<C> type,Consumer<C> consumer
    ) {
        if (type == null) {
            throw new NullPointerException("true is null!");
        }
        if (consumer == null) {
            throw new NullPointerException("consumer is null!");
        }
        if (!getUpdateData().isKeep()) return;
        var callbacks = getUpdateData().getGlfwCallbacks();
        if (callbacks == null) return;
        for (Callback callback : callbacks) {
            if (callback == null) continue;
            if (!type.isInstance(callback)) continue;
            consumer.accept((C) callback);
        }

    }

}
