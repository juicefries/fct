package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.ContentScaleEvent;

@FunctionalInterface
public interface ContentScaleListener extends CallbackListener {

    void scale(ContentScaleEvent e);

}
