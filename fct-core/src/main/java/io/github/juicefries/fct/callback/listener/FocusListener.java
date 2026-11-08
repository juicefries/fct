package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.FocusEvent;

@FunctionalInterface
public interface FocusListener extends CallbackListener {

    void focused(FocusEvent e);

}
