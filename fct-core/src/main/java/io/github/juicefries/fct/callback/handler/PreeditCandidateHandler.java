package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.PreeditCandidateEvent;
import io.github.juicefries.fct.callback.listener.PreeditCandidateListener;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWPreeditCandidateCallback;

/**
 * 输入法候选列表处理类。
 *
 * <p>把 GLFW 的候选数量/选中下标/分页信息包装成 {@link PreeditCandidateEvent} 并分发给监听器。</p>
 *
 * @since 1.0.7
 */
public final class PreeditCandidateHandler extends GLFWPreeditCandidateCallback implements Handler {

    public PreeditCandidateHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetPreeditCandidateCallback(window, this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof PreeditCandidateEvent e)) return;
        var container = Event.get(e.window());
        var listeners = container.getListeners(PreeditCandidateListener.class);
        Array.forArr(listeners, listener -> listener.candidate(e.copy()));
    }

    @Override
    public void invoke(long window, int candidates_count, int selected_index,
                       int page_start, int page_size) {
        var container = Event.get(window);
        var listeners = container.getListeners(PreeditCandidateListener.class);
        var event = PreeditCandidateEvent.candidate(window, candidates_count,
                selected_index, page_start, page_size);
        Array.forArr(listeners, listener -> listener.candidate(event.copy()));
    }
}
