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
// Data 2026/08/20 22:39
//

package io.github.juicefries.fct.image;

import io.github.juicefries.fct.Graphics;
import io.github.juicefries.fct.Image;
import java.nio.ByteBuffer;

@Deprecated
public class BufferedImage extends Image {

    Format format;
    ByteBuffer buffer;
    public BufferedImage(int width,int height,Format format) {

    }

    public BufferedImage(int width,int height) {
        this(width,height,Format.RGBA);
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public Format getFormat() {
        return null;
    }

    @Override
    public ByteBuffer getData() {
        return null;
    }

    @Override
    public int getSizeInBytes() {
        return 0;
    }

    @Override
    public int getBytesPerPixel() {
        return 0;
    }

    public Graphics createGraphics() {
        return null;
    }
}
