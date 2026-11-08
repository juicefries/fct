package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.ButtonEvent;

public interface ButtonListener extends CallbackListener {

    void press(ButtonEvent e);

    void release(ButtonEvent e);

}
