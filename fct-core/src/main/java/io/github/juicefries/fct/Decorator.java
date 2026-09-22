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

package io.github.juicefries.fct;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import com.sun.jna.Function;
import com.sun.jna.Memory;
import com.sun.jna.NativeLibrary;
import com.sun.jna.platform.unix.X11;
import org.lwjgl.glfw.GLFWNativeCocoa;
import org.lwjgl.glfw.GLFWNativeX11;

@Deprecated
public interface Decorator {

    Decorator apply(long wpv) throws DecoratorException;

    default Decorator apply(Window window) throws DecoratorException {
        if (window == null) {
            throw new NullPointerException("window is null!");
        }
        return apply(Toolkit.getWindow(window));
    }

    @Contract(value = " -> new", pure = true)
    static @NotNull UtilityDecorator utility() {
        return new UtilityDecorator();
    }

    static void utility(long wpv) throws DecoratorException {
        utility().apply(wpv);
    }




    class UtilityDecorator implements Decorator {

        private static final long WS_EX_TOOLWINDOW = 0x00000080L;
        private static final long WS_EX_APPWINDOW   = 0x00040000L;

        @Override
        public Decorator apply(long wpv) throws DecoratorException {
            if (wpv == MemoryUtil.NULL) {
                throw new DecoratorException("Invalid window!");
            }

            try {
                switch (Platform.get()) {
                    case Platform.WINDOWS -> applyWin32(wpv);
                    case Platform.MACOSX -> applyCocoa(wpv);
                    case Platform.LINUX -> applyX11(wpv);
                    default -> throw new DecoratorException("Unsupported platform!");
                }
            } catch (Exception e) {
                throw new DecoratorException(e);
            }
            return this;
        }

        // ===================== Windows =====================

        private void applyWin32(long wpv) throws DecoratorException {
            long win = GLFWNativeWin32.glfwGetWin32Window(wpv);
            if (win == MemoryUtil.NULL) {
                throw new DecoratorException("Failed to get Win32 window handle!");
            }

            WinDef.HWND hwnd = new WinDef.HWND(Pointer.createConstant(win));
            User32 user32 = User32.INSTANCE;

            long exStyle = user32.GetWindowLongPtr(hwnd, WinUser.GWL_EXSTYLE).longValue();
            exStyle |= WS_EX_TOOLWINDOW;    // 无任务栏 + 小标题栏
            exStyle &= ~WS_EX_APPWINDOW;    // 去应用窗口标志
            user32.SetWindowLongPtr(hwnd, WinUser.GWL_EXSTYLE, Pointer.createConstant(exStyle));

            user32.SetWindowPos(hwnd, null, 0, 0, 0, 0,
                    WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE
                            | WinUser.SWP_NOZORDER | WinUser.SWP_FRAMECHANGED);
        }

        // ===================== macOS =====================

        private void applyCocoa(long wpv) throws DecoratorException {
            long win = GLFWNativeCocoa.glfwGetCocoaWindow(wpv);
            if (win == MemoryUtil.NULL) {
                throw new DecoratorException("Failed to get Cocoa window!");
            }

            // 工具窗口 = NSFloatingWindowLevel(3)
            long sel = ObjC.INSTANCE.sel_registerName("setLevel:");
            NativeLibrary lib = NativeLibrary.getInstance("objc");
            Function f = Function.getFunction(lib.getFunction("objc_msgSend"));
            f.invokeVoid(new Object[]{win, sel, 3L});
        }

        // ===================== Linux X11 =====================

        private void applyX11(long wpv) throws DecoratorException {
            long win = GLFWNativeX11.glfwGetX11Window(wpv);
            long display = GLFWNativeX11.glfwGetX11Display();
            if (win == MemoryUtil.NULL || display == MemoryUtil.NULL) {
                throw new DecoratorException("Failed to get X11 window/display!");
            }

            X11 x11 = X11.INSTANCE;
            X11.Display dpy = new X11.Display();
            dpy.setPointer(Pointer.createConstant(display));
            X11.Window window = new X11.Window(win);

            X11.Atom propAtom = x11.XInternAtom(dpy, "_NET_WM_WINDOW_TYPE", false);
            X11.Atom typeAtom = x11.XInternAtom(dpy, "_NET_WM_WINDOW_TYPE_UTILITY", false);

            Memory data = new Memory(8);
            data.setLong(0, typeAtom.longValue());

            x11.XChangeProperty(dpy, window, propAtom, X11.XA_ATOM, 32,
                    X11.PropModeReplace, data, 1);
        }

        interface ObjC extends Library {
            ObjC INSTANCE = Native.load("objc", ObjC.class);
            long sel_registerName(String name);
        }

    }


}
