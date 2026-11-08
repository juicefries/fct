package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.ScrollEvent;

@FunctionalInterface
public interface ScrollListener extends CallbackListener {

    void scroll(ScrollEvent e);

}
