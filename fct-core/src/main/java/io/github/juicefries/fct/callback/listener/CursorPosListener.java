package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.CursorPosEvent;

@FunctionalInterface
public interface CursorPosListener extends CallbackListener {

    void pos(CursorPosEvent e);

}
