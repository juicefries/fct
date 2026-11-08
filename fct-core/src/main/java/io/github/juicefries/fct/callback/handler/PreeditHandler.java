package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.PreeditEvent;
import io.github.juicefries.fct.callback.listener.PreeditListener;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWPreeditCallback;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

/**
 * 输入法组合文本处理类。
 *
 * <p>把 GLFW 原生的 UTF-32 码点数组、块长度数组解码后构造 {@link PreeditEvent} 并分发给监听器。</p>
 *
 * @since 1.0.7
 */
public final class PreeditHandler extends GLFWPreeditCallback implements Handler {

    public PreeditHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetPreeditCallback(window, this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof PreeditEvent e)) return;
        var container = Event.get(e.window());
        var listeners = container.getListeners(PreeditListener.class);
        Array.forArr(listeners, listener -> listener.preedit(e.copy()));
    }

    @Override
    public void invoke(long window, int preedit_count, long preedit_string,
                       int block_count, long block_sizes, int focused_block, int caret) {
        StringBuilder builder = new StringBuilder(preedit_count);
        if (preedit_string != MemoryUtil.NULL && preedit_count > 0) {
            IntBuffer buffer = MemoryUtil.memIntBuffer(preedit_string, preedit_count);
            for (int i = 0; i < preedit_count; i++) {
                builder.appendCodePoint(buffer.get(i));
            }
        }

        int[] sizes = new int[0];
        if (block_sizes != MemoryUtil.NULL && block_count > 0) {
            sizes = new int[block_count];
            MemoryUtil.memIntBuffer(block_sizes, block_count).get(sizes);
        }

        var container = Event.get(window);
        var listeners = container.getListeners(PreeditListener.class);
        var event = PreeditEvent.preedit(window, builder.toString(), sizes, focused_block, caret);
        Array.forArr(listeners, listener -> listener.preedit(event.copy()));
    }
}
