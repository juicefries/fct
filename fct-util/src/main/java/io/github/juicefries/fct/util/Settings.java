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

package io.github.juicefries.fct.util;

import java.util.HashMap;

public class Settings extends HashMap<String,Object> {

    // ========================= SET =========================

    public void putString(String key,String value) {
        put(key,value);
    }

    public void putInteger(String key,int value) {
        put(key,value);
    }

    public void putBoolean(String key,boolean value) {
        put(key,value);
    }

    public void putFloat(String key, float value) {
        put(key,value);
    }

    public void putLong(String key,long value) {
        put(key,value);
    }

    public void putShort(String key,short value) {
        put(key,value);
    }

    public void putDouble(String key,double value) {
        put(key,value);
    }

    public void putChar(String key,char value) {
        put(key,value);
    }

    public void putByte(String key,byte value) {
        put(key,value);
    }

    public void putBytes(String key,byte[] value) {
        put(key,value);
    }

    // ========================= GET =========================

    public Object getObject(String key,Object defaultValue) {
        Object obj = get(key);

        if (obj == null) {
            return defaultValue;
        }
        return obj;
    }

    public String getString(String key, String defaultValue) {
        Object obj = get(key);

        if (obj == null) {
            return defaultValue;
        }
        return (String) obj;
    }

    public String getString(String key) {
        return getString(key,null);
    }

    public int getInteger(String key, int defaultValue) {
        Object obj = get(key);

        if (obj == null) {
            return defaultValue;
        }

        return (Integer) obj;
    }

    public int getInteger(String key) {
        return getInteger(key,0);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object obj = get(key);

        if (obj == null) {
            return defaultValue;
        }

        return (Boolean) obj;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public float getFloat(String key, float defaultValue) {
        Object obj = get(key);

        if (obj == null) {
            return defaultValue;
        }

        return (Float) obj;
    }

    public float getFloat(String key) {
        return getFloat(key, 0f);
    }

    public long getLong(String key,long defaultValue) {
        Object obj = get(key);

        if (obj == null) {
            return defaultValue;
        }

        return (Long) obj;
    }

    public long getLong(String key) {
        return getLong(key,0L);
    }

    public short getShort(String key, short defaultValue) {
        Object obj = get(key);

        if (obj == null) {
            return defaultValue;
        }

        return (Short) obj;
    }

    public short getShort(String key) {
        return getShort(key, (short) 0);
    }

    public double getDouble(String key,double defaultValue) {
        Object obj = get(key);

        if (obj == null) {
            return defaultValue;
        }

        return (Double) obj;
    }

    public char getChar(String key,char defaultValue) {
        Object obj = get(key);

        if (obj == null) {
            return defaultValue;
        }

        return (Character) obj;
    }

    public char getChar(String key) {
        return getChar(key,'\u0000');
    }

    public byte getByte(String key,byte defaultValue) {
        Object obj = get(key);

        if (obj == null) {
            return defaultValue;
        }

        return (Byte) obj;
    }

    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    public byte[] getBytes(String key,byte[] defaultValue) {
        Object obj = get(key);

        if (obj == null) {
            return defaultValue;
        }

        return (byte[]) obj;
    }

    public byte[] getBytes(String key) {
        return getBytes(key,null);
    }

    // ========================= OTM =========================

}
