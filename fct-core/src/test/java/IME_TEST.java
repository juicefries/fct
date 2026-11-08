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

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.opengl.GL;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.GLFW.*;

void main() {
    glfwInit();

    long window = glfwCreateWindow(800,600,"ime test",NULL,NULL);
    glfwMakeContextCurrent(window);
    GL.createCapabilities();
    glfwSwapInterval(GLFW_TRUE);

    var ime = new boolean[1];
    glfwSetKeyCallback(window, (_, key, _, action, _) -> {
        if (key == GLFW_KEY_T && action == GLFW_PRESS) {
            ime[0] = !ime[0];
            if (ime[0]) {
                GLFW.glfwSetInputMode(window, GLFW.GLFW_IME, GLFW_TRUE);
            } else {
                glfwSetInputMode(window,GLFW_IME,GLFW_FALSE);
            }
        }
    });
    glfwSetIMEStatusCallback(window,_ -> {
        int i = glfwGetInputMode(window, GLFW_IME);
        glfwSetWindowTitle(window,"" + i);
    });


    glfwShowWindow(window);
    while (!glfwWindowShouldClose(window)) {

        //if (!ime[0]) {
            //GLFW.glfwSetInputMode(window, GLFW.GLFW_IME, GLFW.GLFW_FALSE);
        //}
        int i = glfwGetInputMode(window, GLFW_IME);
        IO.print("ime:%d\r".formatted(i));

        glfwSwapBuffers(window);
        glfwPollEvents();
    }
    glfwHideWindow(window);

    glfwDestroyWindow(window);
    glfwTerminate();
}