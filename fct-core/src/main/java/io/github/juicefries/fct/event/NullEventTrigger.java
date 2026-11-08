//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 17:09
//

package io.github.juicefries.fct.event;

import io.github.juicefries.fct.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

import java.util.concurrent.atomic.AtomicBoolean;

public final class NullEventTrigger extends EventTrigger {

    private static final Logger logger = LogManager.getLogger(NullEventTrigger.class);
    private final AtomicBoolean initialize = new AtomicBoolean(false);

    Window window;

    public NullEventTrigger() {
        super();
    }

    @Contract("null -> fail")
    @Override
    public void bind(Window window) {
        if (window == null) {
            throw new NullPointerException("mgr is null!");
        }
        if (isInitialize()) {
            try {
                dispose();
            } catch (Exception e) {
                logger.error("An error occurred while unregistering the old EventTrigger.", e);
                return;
            }
        }
        this.window = window;
    }

    @Override
    public void initialize() {
        if (isInitialize()) {
            throw new IllegalStateException("CallbackManager have been initialized!");
        }
        initialize.set(true);
    }

    @Override
    public void dispose() {
        if (!isInit()) return;
        window = null;
        initialize.set(false);
    }

    @Override
    public boolean isInitialize() {
        return initialize.get();
    }

}
