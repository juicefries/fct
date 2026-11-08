package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.KeyEvent;

public interface KeyListener extends CallbackListener {

    /**
     * 按下
     * @param e KED
     * @since 1.0.7
     */
    void press(KeyEvent e);

    /**
     * 重复
     * @param e KED
     * @since 1.0.7
     */
    void repeat(KeyEvent e);

    /**
     * 释放
     * @param e KED
     * @since 1.0.7
     */
    void release(KeyEvent e);

}
