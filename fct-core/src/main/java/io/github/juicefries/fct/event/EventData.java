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

package io.github.juicefries.fct.event;

import io.github.juicefries.fct.Toolkit;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.Util;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 事件数据
 * <p>
 * 表示监听器提供的事件数据
 * </p>
 *
 * @author juicefries
 * @since 0.0.1
 */
public interface EventData extends Event, Copyable, Cloneable, Readonly {

    @ApiStatus.Internal
    @ApiSign.InternalApi(since = "1.0.1")
    final class _event_data_util implements Event {

        final static Lock lock = Lock.create();

        static Map<String, Long> _event_ids = new ConcurrentHashMap<>();
        static Map<Long,String > _event_id_mapping = new ConcurrentHashMap<>();

        static {
            Toolkit.initialize();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                _event_ids.clear();
                _event_id_mapping.clear();
            },"FCT-AutomaticStreamCleanup-Event"));
        }

        public static long _build_event_id(String id) {
            synchronized (lock) {
                if (_event_ids.containsKey(id)) {
                    throw new IllegalArgumentException("The ID has already been registered!");
                }
                long _id = Util.turn(id);
                _event_ids.put(id, _id);
                _event_id_mapping.put(_id, id);
                return _id;
            }
        }

        @Contract(" -> fail")
        private _event_data_util() {
            System.exit(666666666);
        }

        @Contract(value = " -> new", pure = true)
        public static @NotNull Map<String, Long> get_event_ids() {
            return new HashMap<>(_event_ids);
        }

        @Contract(value = " -> new", pure = true)
        public static @NotNull Map<Long, String> get_event_id_mapping() {
            return new HashMap<>(_event_id_mapping);
        }
    }

    /**
     * 获取事件类型
     *
     * @return 事件类型
     * @since 0.0.1
     */
    long getType();

    /**
     * 复制事件
     *
     * @return 事件副本
     * @since 0.0.1
     */
    @Override
    EventData copy();

    /**
     * 复制事件
     *
     * @return 事件副本
     * @since 0.0.1
     */
    EventData clone();

    @ApiStatus.Experimental
    static String toEventTypeString(long type) {
        return _event_data_util._event_id_mapping.get(type);
    }

}
