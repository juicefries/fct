package io.github.juicefries.fct.callback.listener;

import io.github.juicefries.fct.callback.event.PreeditCandidateEvent;

/**
 * 输入法候选列表元信息监听器。
 *
 * @since 1.0.7
 */
@FunctionalInterface
public interface PreeditCandidateListener extends CallbackListener {

    void candidate(PreeditCandidateEvent e);

}
