package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.DropEvent;

@FunctionalInterface
public interface DropListener extends CallbackListener {

    void drop(DropEvent e);

}
