package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.CursorEnterEvent;


public interface CursorEnterListener extends CallbackListener {

    void enter(CursorEnterEvent e);

    void leave(CursorEnterEvent e);


}
