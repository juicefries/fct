package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.PreeditEvent;

/**
 * 输入法组合文本监听器。
 *
 * <p>组合中文本变化、块/光标变化、组合结束（{@link PreeditEvent#text()} 为空串）都会回调。</p>
 *
 * @since 1.0.7
 */
@FunctionalInterface
public interface PreeditListener extends CallbackListener {

    void preedit(PreeditEvent e);

}
