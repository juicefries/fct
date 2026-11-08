package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.ToggleEvent;

public interface ToggleListener extends CallbackListener {

    /**
     * 最大化
     * @param e 数据
     */
    void maximized(ToggleEvent e);


    /**
     * 最大化
     * @param e 最小化
     */
    void minimized(ToggleEvent e);

    /**
     * 恢复
     * @param e 数据
     */
    void restored(ToggleEvent e);

}
