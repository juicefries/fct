package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.IMEStatusEvent;

@FunctionalInterface
public interface IMEStatusListener extends CallbackListener {

    void event(IMEStatusEvent e);

}
