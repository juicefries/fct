package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.CharEvent;

@FunctionalInterface
public interface CharListener extends CallbackListener {

    void input(CharEvent e);

}
