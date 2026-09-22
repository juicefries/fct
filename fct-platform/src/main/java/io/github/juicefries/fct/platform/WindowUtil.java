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
// Data 2026/09/12 13:36
//

package io.github.juicefries.fct.platform;

import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Util;
import org.lwjgl.glfw.GLFWNativeCocoa;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWNativeX11;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;

@Deprecated
public final class WindowUtil {

    public final static int PLATFORM_NOT_SUPPORTED = 0x0e1ffff;
    public final static int SETTINGS_SUCCESS = 0x0e2ffff;
    public final static int ERROR_OCCURRED_DURING_SETUP = 0x0e3ffff;
    public final static int INVALID_PARAMETER = 0x0e4ffff;

    /// 普通类型浮动容器
    public final static long ORDINARY = Util.turn("Ordinary");
    /// 工具类型浮动容器
    public final static long UTILITY = Util.turn("Utility");
    /// 弹出类型浮动容器
    public final static long POPUP = Util.turn("Popup");
    /// 去最大最小化，有任务栏
    public final static long DIALOG = Util.turn("Dialog");
    /// 去最大最小化，无任务栏
    public final static long TOOL_DIALOG = Util.turn("ToolDialog");

    private final static long WS_EX_TOOLWINDOW = 0x00000080L;
    private final static long WS_EX_APPWINDOW   = 0x00040000L;
    private final static long WS_EX_TOPMOST     = 0x00000008L;
    private final static long WS_MAXIMIZEBOX    = 0x00010000L;
    private final static long WS_MINIMIZEBOX    = 0x00020000L;

    private final static long WS_CAPTION     = 0x00C00000L;
    private final static long WS_SYSMENU     = 0x00080000L;
    private final static long WS_THICKFRAME  = 0x00040000L;


    // 装饰标志位掩码
    public final static long DEC_TITLEBAR   = 1L << 0;  // 标题栏
    public final static long DEC_CLOSE      = 1L << 1;  // 关闭按钮
    public final static long DEC_MINIMIZE   = 1L << 2;  // 最小化按钮
    public final static long DEC_MAXIMIZE   = 1L << 3;  // 最大化按钮
    public final static long DEC_RESIZABLE  = 1L << 4;  // 可调整大小
    public final static long DEC_TASKBAR    = 1L << 5;  // 任务栏图标
    public final static long DEC_TOPMOST    = 1L << 6;  // 置顶


    public static int setWindowType(long wpv, long type) {
        if (wpv == MemoryUtil.NULL) {
            return INVALID_PARAMETER;
        }
        if (!Array.contains(type, ORDINARY, UTILITY, POPUP, DIALOG, TOOL_DIALOG)) {
            return INVALID_PARAMETER;
        }

        switch (Platform.get()) {
            case Platform.WINDOWS -> {
                long win = GLFWNativeWin32.glfwGetWin32Window(wpv);
                if (win == MemoryUtil.NULL) return ERROR_OCCURRED_DURING_SETUP;
                return setWin32Type(new WinDef.HWND(Pointer.createConstant(win)), type);
            }
            case Platform.MACOSX -> {
                long win = GLFWNativeCocoa.glfwGetCocoaWindow(wpv);
                if (win == MemoryUtil.NULL) return ERROR_OCCURRED_DURING_SETUP;
                return setCocoaType(win, type);
            }
            case Platform.LINUX -> {
                long win = GLFWNativeX11.glfwGetX11Window(wpv);
                if (win == MemoryUtil.NULL) return ERROR_OCCURRED_DURING_SETUP;
                return setX11Type(win, type);
            }
            default -> {
                return PLATFORM_NOT_SUPPORTED;
            }
        }
    }

    public static int setDecorations(long wpv, long decorations) {
        if (wpv == MemoryUtil.NULL) {
            return INVALID_PARAMETER;
        }

        switch (Platform.get()) {
            case Platform.WINDOWS -> {
                long win = GLFWNativeWin32.glfwGetWin32Window(wpv);
                if (win == MemoryUtil.NULL) return ERROR_OCCURRED_DURING_SETUP;
                return setWin32Decorations(new WinDef.HWND(Pointer.createConstant(win)), decorations);
            }
            default -> {
                return PLATFORM_NOT_SUPPORTED;
            }
        }
    }


    // ===================== Windows =====================

    private static int setWin32Decorations(WinDef.HWND hwnd, long dec) {
        User32 user32 = User32.INSTANCE;

        // 普通样式
        long style = user32.GetWindowLongPtr(hwnd, WinUser.GWL_STYLE).longValue();

        style = (dec & DEC_TITLEBAR)  != 0 ? style | WS_CAPTION      : style & ~WS_CAPTION;
        style = (dec & DEC_CLOSE)     != 0 ? style | WS_SYSMENU      : style & ~WS_SYSMENU;
        style = (dec & DEC_MINIMIZE)  != 0 ? style | WS_MINIMIZEBOX  : style & ~WS_MINIMIZEBOX;
        style = (dec & DEC_MAXIMIZE)  != 0 ? style | WS_MAXIMIZEBOX  : style & ~WS_MAXIMIZEBOX;
        style = (dec & DEC_RESIZABLE) != 0 ? style | WS_THICKFRAME   : style & ~WS_THICKFRAME;

        user32.SetWindowLongPtr(hwnd, WinUser.GWL_STYLE, Pointer.createConstant(style));

        // 扩展样式
        long exStyle = user32.GetWindowLongPtr(hwnd, WinUser.GWL_EXSTYLE).longValue();

        if ((dec & DEC_TASKBAR) != 0) {
            exStyle &= ~WS_EX_TOOLWINDOW;
            exStyle |= WS_EX_APPWINDOW;
        } else {
            exStyle |= WS_EX_TOOLWINDOW;
            exStyle &= ~WS_EX_APPWINDOW;
        }

        exStyle = (dec & DEC_TOPMOST) != 0 ? exStyle | WS_EX_TOPMOST : exStyle & ~WS_EX_TOPMOST;

        user32.SetWindowLongPtr(hwnd, WinUser.GWL_EXSTYLE, Pointer.createConstant(exStyle));

        // 应用
        user32.SetWindowPos(hwnd, null, 0, 0, 0, 0,
                WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE
                        | WinUser.SWP_NOZORDER | WinUser.SWP_FRAMECHANGED);
        return SETTINGS_SUCCESS;
    }


    private static int setWin32Type(WinDef.HWND hwnd, long type) {
        User32 user32 = User32.INSTANCE;

        // 普通样式：控制最大/最小化按钮
        long style = user32.GetWindowLongPtr(hwnd, WinUser.GWL_STYLE).longValue();
        if (type == DIALOG || type == TOOL_DIALOG) {
            style &= ~(WS_MAXIMIZEBOX | WS_MINIMIZEBOX);
        } else {
            style |= WS_MAXIMIZEBOX | WS_MINIMIZEBOX;
        }
        user32.SetWindowLongPtr(hwnd, WinUser.GWL_STYLE, Pointer.createConstant(style));

        // 扩展样式：控制工具窗口 / 置顶 / 任务栏
        long exStyle = user32.GetWindowLongPtr(hwnd, WinUser.GWL_EXSTYLE).longValue();

        if (type == ORDINARY) {
            exStyle &= ~WS_EX_TOOLWINDOW;
            exStyle |= WS_EX_APPWINDOW;
        } else if (type == UTILITY) {
            exStyle |= WS_EX_TOOLWINDOW;
            exStyle &= ~WS_EX_APPWINDOW;
        } else if (type == POPUP) {
            exStyle |= WS_EX_TOOLWINDOW;
            exStyle |= WS_EX_TOPMOST;
        } else if (type == DIALOG) {
            exStyle &= ~WS_EX_TOOLWINDOW;
            exStyle &= ~WS_EX_TOPMOST;
        } else { // TOOL_DIALOG：普通标题栏，无任务栏
            exStyle &= ~WS_EX_TOOLWINDOW;
            exStyle &= ~WS_EX_TOPMOST;
        }
        user32.SetWindowLongPtr(hwnd, WinUser.GWL_EXSTYLE, Pointer.createConstant(exStyle));

        // 应用
        user32.SetWindowPos(hwnd, null, 0, 0, 0, 0,
                WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE
                        | WinUser.SWP_NOZORDER | WinUser.SWP_FRAMECHANGED);

        // TOOL_DIALOG 额外用 COM 移除任务栏图标（保留普通标题栏）
        if (type == TOOL_DIALOG) {
            return removeTaskbarIcon(hwnd);
        }
        return SETTINGS_SUCCESS;
    }

    private static int removeTaskbarIcon(WinDef.HWND hwnd) {
        Ole32 ole32 = Ole32.INSTANCE;

        ole32.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);

        Guid.CLSID clsid = new Guid.CLSID("{56FDF344-FD6D-11d0-958A-006097C9A090}"); // CLSID_TaskbarList
        Guid.IID iid = new Guid.IID("{56FDF342-FD6D-11d0-958A-006097C9A090}");       // IID_ITaskbarList

        PointerByReference ppv = new PointerByReference();
        WinNT.HRESULT hr = ole32.CoCreateInstance(clsid, null,
                WTypes.CLSCTX_INPROC_SERVER, iid, ppv);

        if (hr.intValue() != 0) {
            ole32.CoUninitialize();
            return ERROR_OCCURRED_DURING_SETUP;
        }

        Pointer pv = ppv.getValue();
        Pointer vtable = pv.getPointer(0);

        // DeleteTab 是 ITaskbarList vtable 第 6 个方法（索引 5）
        Pointer deleteTab = vtable.getPointer(5L * Native.POINTER_SIZE);
        Function f = Function.getFunction(deleteTab);
        f.invokeInt(new Object[]{pv, hwnd.getPointer()});

        // Release 是 IUnknown 第 3 个方法（索引 2）
        Pointer release = vtable.getPointer(2L * Native.POINTER_SIZE);
        Function rf = Function.getFunction(release);
        rf.invokeInt(new Object[]{pv});

        ole32.CoUninitialize();
        return SETTINGS_SUCCESS;
    }

    // ===================== macOS =====================

    private static int setCocoaType(long window, long type) {
        long level;
        if (type == ORDINARY || type == DIALOG || type == TOOL_DIALOG) {
            level = 0L;      // NSNormalWindowLevel
        } else if (type == UTILITY) {
            level = 3L;      // NSFloatingWindowLevel
        } else {             // POPUP
            level = 101L;    // NSPopUpMenuWindowLevel
        }

        long sel = ObjC.INSTANCE.sel_registerName("setLevel:");
        NativeLibrary lib = NativeLibrary.getInstance("objc");
        Function f = Function.getFunction(lib.getFunction("objc_msgSend"));
        f.invokeVoid(new Object[]{window, sel, level});
        return SETTINGS_SUCCESS;
    }

    private interface ObjC extends Library {
        ObjC INSTANCE = Native.load("objc", ObjC.class);
        long sel_registerName(String name);
    }

    // ===================== Linux X11 =====================

    private static int setX11Type(long window, long type) {
        long display = GLFWNativeX11.glfwGetX11Display();
        if (display == MemoryUtil.NULL) return ERROR_OCCURRED_DURING_SETUP;

        X11 x11 = X11.INSTANCE;
        X11.Display dpy = new X11.Display();
        dpy.setPointer(Pointer.createConstant(display));

        X11.Window win = new X11.Window(window);

        X11.Atom propAtom = x11.XInternAtom(dpy, "_NET_WM_WINDOW_TYPE", false);

        String typeName;
        if (type == UTILITY || type == TOOL_DIALOG) {
            typeName = "_NET_WM_WINDOW_TYPE_UTILITY";
        } else if (type == POPUP) {
            typeName = "_NET_WM_WINDOW_TYPE_POPUP_MENU";
        } else if (type == DIALOG) {
            typeName = "_NET_WM_WINDOW_TYPE_DIALOG";
        } else {
            typeName = "_NET_WM_WINDOW_TYPE_NORMAL";
        }
        X11.Atom typeAtom = x11.XInternAtom(dpy, typeName, false);

        Memory data = new Memory(8);
        data.setLong(0, typeAtom.longValue());

        x11.XChangeProperty(dpy, win, propAtom, X11.XA_ATOM, 32,
                X11.PropModeReplace, data, 1);
        return SETTINGS_SUCCESS;
    }
}
