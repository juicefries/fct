/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 juicefries
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

//
// Created by juicefries
// The project name is fct
// Data 2026/08/09 01:49
//

package io.github.juicefries.fct.input;

import io.github.juicefries.fct.util.Util;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import org.lwjgl.system.MemoryUtil;


import static io.github.juicefries.fct.input.KeyboardData.KEYBOARD_CHAR_EVENT;
import static io.github.juicefries.fct.input.KeyboardData.KEYBOARD_CHAR_MODS_EVENT;
import static io.github.juicefries.fct.input.KeyboardData.KEYBOARD_PRESS_EVENT;
import static io.github.juicefries.fct.input.KeyboardData.KEYBOARD_RELEASE_EVENT;
import static io.github.juicefries.fct.input.KeyboardData.KEYBOARD_REPEAT_EVENT;
import static io.github.juicefries.fct.input.KeyboardData.NOT_MATCH_EVENT_FIELD_INT_VALUE;
import static io.github.juicefries.fct.input.MouseData.MOUSE_BUTTON_EVENT;
import static io.github.juicefries.fct.input.MouseData.MOUSE_BUTTON_PRESS_EVENT;
import static io.github.juicefries.fct.input.MouseData.MOUSE_BUTTON_RELEASE_EVENT;
import static io.github.juicefries.fct.input.MouseData.MOUSE_CURSOR_ENTER_EVENT;
import static io.github.juicefries.fct.input.MouseData.MOUSE_CURSOR_LEAVE_EVENT;
import static io.github.juicefries.fct.input.MouseData.MOUSE_CURSOR_POS_EVENT;
import static io.github.juicefries.fct.input.MouseData.MOUSE_DROP_EVENT;
import static io.github.juicefries.fct.input.MouseData.MOUSE_SCROLL_EVENT;
import static io.github.juicefries.fct.input.WindowData.WINDOW_CLOSE_EVENT;
import static io.github.juicefries.fct.input.WindowData.WINDOW_CONTENT_SCALE_EVENT;
import static io.github.juicefries.fct.input.WindowData.WINDOW_FOCUS_EVENT;
import static io.github.juicefries.fct.input.WindowData.WINDOW_FRAMEBUFFER_SIZE_EVENT;
import static io.github.juicefries.fct.input.WindowData.WINDOW_MAXIMIZED;
import static io.github.juicefries.fct.input.WindowData.WINDOW_MAXIMIZED_EVENT;
import static io.github.juicefries.fct.input.WindowData.WINDOW_MINIMIZED;
import static io.github.juicefries.fct.input.WindowData.WINDOW_MINIMIZED_EVENT;
import static io.github.juicefries.fct.input.WindowData.WINDOW_NORMAL;
import static io.github.juicefries.fct.input.WindowData.WINDOW_NORMAL_EVENT;
import static io.github.juicefries.fct.input.WindowData.WINDOW_POS_EVENT;
import static io.github.juicefries.fct.input.WindowData.WINDOW_REFRESH_EVENT;
import static io.github.juicefries.fct.input.WindowData.WINDOW_SIZE_EVENT;

/**
 * 负责处理原始数据
 * @deprecated 现已经弃用
 */
public class InputManager {

    private final static Logger logger = Logger.getLogger(InputManager.class.getCanonicalName());
    public final static String WINDOW = "WindowInput";
    public final static String KEYBOARD = "KeyboardInput";
    public final static String MOUSE = "MouseInput";

    private final Set<WindowInput> windowInputs = new HashSet<>();
    private final Set<KeyboardInput> keyboardInputs = new HashSet<>();
    private final Set<MouseInput> mouseInputs = new HashSet<>();

    private long context;

    public InputManager() {

    }

    // ========================= OTM =========================

    public void initialization(long ctx) {
        context = ctx;
    }


    public void initCallback() {
        new Thread(() -> initWindowCallback(context)).start();
        new Thread(() -> initKeyboardCallback(context)).start();
        new Thread(() -> initMouseCallback(context)).start();
    }

    protected void initWindowCallback(long window) {
        GLFW.glfwSetWindowCloseCallback(window, value0 -> {
            if (windowInputs.isEmpty()) return;

            for (WindowInput listener : windowInputs) {
                if (listener instanceof WindowLifeInput l) {
                    l.close(new WindowData(value0, WINDOW_CLOSE_EVENT));
                }
            }
        });

        GLFW.glfwSetWindowSizeCallback(window, (value0, value1, value2) -> {
            if (windowInputs.isEmpty()) return;

            for (WindowInput listener : windowInputs) {
                if (listener instanceof WindowStateInput l) {
                    l.size(new WindowData(value0, value1, value2, WINDOW_SIZE_EVENT));
                }
            }
        });
        GLFW.glfwSetWindowPosCallback(window, (value0, value1, value2) -> {
            if (windowInputs.isEmpty()) return;

            for (WindowInput listener : windowInputs) {
                if (listener instanceof WindowStateInput l) {
                    l.move(new WindowData(value0, value1, value2, WINDOW_POS_EVENT));
                }
            }
        });
        GLFW.glfwSetWindowFocusCallback(window,(value0, value1) -> {
            if (windowInputs.isEmpty()) return;

            for (WindowInput listener : windowInputs) {
                if (listener instanceof WindowStateInput l) {
                    l.focus(new WindowData(value0, value1, WINDOW_FOCUS_EVENT));
                }
            }
        });
        GLFW.glfwSetWindowRefreshCallback(window, value0 -> {
            if (windowInputs.isEmpty()) return;

            for (WindowInput listener : windowInputs) {
                if (listener == null) continue;
                if (listener instanceof WindowStateInput l) {
                    l.refresh(new WindowData(value0, WINDOW_REFRESH_EVENT));
                }
            }
        });
        GLFW.glfwSetWindowContentScaleCallback(window, (value0, value1, value2) -> {
            if (windowInputs.isEmpty()) return;

            for (WindowInput listener : windowInputs) {
                if (listener instanceof WindowStateInput l) {
                    l.contentScale(new WindowData(value0, value1, value2, WINDOW_CONTENT_SCALE_EVENT));
                }
            }
        });
        GLFW.glfwSetWindowMaximizeCallback(window, (value0, maximized) -> {
            if (windowInputs.isEmpty()) return;

            for (WindowInput listener : windowInputs) {
                if (listener instanceof WindowToggleInput l) {
                    if (maximized) {
                        l.maximized(new WindowData(value0, WINDOW_MAXIMIZED, WINDOW_MAXIMIZED_EVENT));
                    } else {
                        l.restored(new WindowData(value0, WINDOW_NORMAL, WINDOW_NORMAL_EVENT));
                    }
                }
            }
        });
        GLFW.glfwSetWindowIconifyCallback(window, (value0, iconified) -> {
            if (windowInputs.isEmpty()) return;

            for (WindowInput listener : windowInputs) {
                if (listener instanceof WindowToggleInput l) {
                    if (iconified) {
                        l.minimized(new WindowData(value0, WINDOW_MINIMIZED, WINDOW_MINIMIZED_EVENT));
                    } else {
                        l.restored(new WindowData(value0, WINDOW_NORMAL, WINDOW_NORMAL_EVENT));
                    }
                }
            }
        });
        GLFW.glfwSetFramebufferSizeCallback(window, (value0, value1, value2) -> {
            if (windowInputs.isEmpty()) return;

            for (WindowInput listener : windowInputs) {
                if (listener instanceof WindowStateInput l) {
                    l.framebufferSize(new WindowData(value0, value1, value2, WINDOW_FRAMEBUFFER_SIZE_EVENT));
                }
            }
        });
    }

    protected void initKeyboardCallback(long window) {
        GLFW.glfwSetKeyCallback(window, (value0, value1, value2, value3, value4) -> {
            if (keyboardInputs.isEmpty()) return;
            long value5 = __keyboardActionConvertType__(value3);
            KeyboardData e = new KeyboardData(value0, value1, value2, value4, value5);

            for (KeyboardInput listener : keyboardInputs) {

                if (value3 == GLFW.GLFW_PRESS) {
                    listener.pressKey(e);
                } else if (value3 == GLFW.GLFW_RELEASE) {
                    listener.loosenKey(e);
                } else if (value3 == GLFW.GLFW_REPEAT) {
                    listener.longPress(e);
                }

            }
        });
        GLFW.glfwSetCharCallback(window, (value0, value1) -> {
            if (keyboardInputs.isEmpty()) return;
            for (KeyboardInput listener : keyboardInputs) {
                listener.inputKey(new KeyboardData(value0, value1, NOT_MATCH_EVENT_FIELD_INT_VALUE, KEYBOARD_CHAR_EVENT));
            }
        });
        GLFW.glfwSetCharModsCallback(window, (value0, value1, value2) -> {
            if (keyboardInputs.isEmpty()) return;
            for (KeyboardInput listener : keyboardInputs) {
                //noinspection deprecation
                listener.inputModsKey(new KeyboardData(value0, value1, value2, KEYBOARD_CHAR_MODS_EVENT));
            }
        });
    }

    protected void initMouseCallback(final long window) {
        GLFW.glfwSetMouseButtonCallback(window, (value0, value1, value2, value3) -> {
            if (mouseInputs.isEmpty()) return;

            for (MouseInput listener : mouseInputs) {
                if (listener instanceof MouseButtonInput l) {
                    boolean action = Util.toBooleanStrict(value2,GLFW.GLFW_PRESS,GLFW.GLFW_RELEASE);

                    l.button(new MouseData(value0,value1,value3,action,MOUSE_BUTTON_EVENT));

                    if (action) {
                        l.press(new MouseData(value0,value1,value3,true,MOUSE_BUTTON_PRESS_EVENT));
                    } else {
                        l.release(new MouseData(value0,value1,value3,false,MOUSE_BUTTON_RELEASE_EVENT));
                    }

                }
            }

        });

        GLFW.glfwSetScrollCallback(window, (value0, value1, value2) -> {
            if (mouseInputs.isEmpty()) return;

            for (MouseInput listener : mouseInputs) {
                if (listener instanceof MouseScrollInput l) {
                    l.scroll(new MouseData(value0,value1,value2,MOUSE_SCROLL_EVENT));
                }
            }

        });
        GLFW.glfwSetCursorPosCallback(window,(value0, value1, value2) -> {
            if (mouseInputs.isEmpty()) return;

            for (MouseInput listener : mouseInputs) {
                if (listener instanceof MouseCursorInput l) {
                    l.cursorPos(new MouseData(value0,value1,value2,MOUSE_CURSOR_POS_EVENT));
                }
            }
        });
        GLFW.glfwSetCursorEnterCallback(window,(value0, value1) -> {
            if (mouseInputs.isEmpty()) return;

            for (MouseInput listener : mouseInputs) {
                if (listener instanceof MouseCursorInput l) {
                    if (value1) {
                        l.cursorEnter(new MouseData(value0, MOUSE_CURSOR_ENTER_EVENT));
                    } else {
                        l.cursorLeave(new MouseData(value0, MOUSE_CURSOR_LEAVE_EVENT));
                    }
                }
            }
        });

        GLFW.glfwSetDropCallback(window, (value0, count, names) -> {
            if (mouseInputs.isEmpty()) return;
            MouseData data = new MouseData(value0,count,names,MOUSE_DROP_EVENT);
            for (MouseInput listener : mouseInputs) {
                if (listener instanceof MouseDropInput l) {
                    l.drop(data);
                }
            }

        });
    }

    @ApiStatus.Internal
    public void pollEvents() {
        GLFW.glfwPollEvents();

    }



    // ========================= SET =========================
    // ========================= GET =========================
    // ========================= OTM =========================

    public void addInput(@MagicConstant(stringValues = {WINDOW,MOUSE,KEYBOARD}) String type, InputEvent l) {
        if (type == null || l == null) {
            return;
        }
        if (!(l instanceof WindowInput) && !(l instanceof MouseInput) && !(l instanceof KeyboardInput)) {
            logger.log(Level.WARNING,"The type of input does not match!");
            return;
        }

        synchronized (this) {
            switch (type) {
                case WINDOW -> {
                    if (l instanceof WindowInput wl) {
                        windowInputs.add(wl);
                    } else {
                        logMismatch(type);
                    }
                }
                case KEYBOARD -> {
                    if (l instanceof KeyboardInput kl) {
                        keyboardInputs.add(kl);
                    } else {
                        logMismatch(type);
                    }
                }
                case MOUSE -> {
                    if (l instanceof MouseInput ml) {
                        mouseInputs.add(ml);
                    } else {
                        logMismatch(type);
                    }
                }
                default -> logger.log(Level.WARNING,"Unknown type: {}", type);
            }
        }
    }

    private void logMismatch(String type) {
        logger.log(Level.WARNING,"Input type mismatch for :{}", type);
    }


    public void addInput(WindowInput listener) {
        addInput(WINDOW,listener);
    }

    public void addInput(MouseInput listener) {
        addInput(MOUSE,listener);
    }

    public void addInput(KeyboardInput listener) {
        addInput(KEYBOARD,listener);
    }


    public void removeInput(String type, InputEvent l) {
        if (type == null || l == null) {
            return;
        }
        if (!(l instanceof WindowInput) && !(l instanceof MouseInput) && !(l instanceof KeyboardInput)) {
            logger.log(Level.WARNING,"The type of input to remove doesn't match!");
            return;
        }
        switch (type) {
            case WINDOW -> {
                if (l instanceof WindowInput wl) {
                    windowInputs.remove(wl);
                } else {
                    logMismatch(type);
                }
            }
            case KEYBOARD -> {
                if (l instanceof KeyboardInput kl) {
                    keyboardInputs.remove(kl);
                } else {
                    logMismatch(type);
                }
            }
            case MOUSE -> {
                if (l instanceof MouseInput ml) {
                    mouseInputs.remove(ml);
                } else {
                    logMismatch(type);
                }
            }
            default -> logger.log(Level.WARNING,"Unknown and mismatched type: {}", type);
        }
    }

    public void removeInput(WindowInput input) {
        removeInput(WINDOW,input);
    }

    public void removeInput(KeyboardInput input) {
        removeInput(KEYBOARD,input);
    }

    public void removeInput(MouseInput input) {
        removeInput(MOUSE,input);
    }

    public void cleanup() {
        if (!windowInputs.isEmpty()) {
            windowInputs.clear();
        }
        if (!keyboardInputs.isEmpty()) {
            keyboardInputs.clear();
        }
        if (!mouseInputs.isEmpty()) {
            mouseInputs.clear();
        }
        context = MemoryUtil.NULL;
    }

    @ApiStatus.Internal()
    public static long __keyboardActionConvertType__(int action) {
        if (action == GLFW.GLFW_PRESS) {
            return KEYBOARD_PRESS_EVENT;
        }
        if (action == GLFW.GLFW_RELEASE) {
            return KEYBOARD_RELEASE_EVENT;
        }
        if (action == GLFW.GLFW_REPEAT) {
            return KEYBOARD_REPEAT_EVENT;
        }
        return 0L;
    }

}

