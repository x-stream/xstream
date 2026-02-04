/*
 * Copyright (C) 2026 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 2nd February 2026 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.xstream.converters.reflection.Unsafe;


/**
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
class SunMiscUnsafe implements Unsafe {

    private static final Unsafe unsafe;
    private final sun.misc.Unsafe sunUnsafe;
    private final Exception exception;
    static {
        sun.misc.Unsafe u = null;
        Exception ex = null;
        try {
            final Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            u = (sun.misc.Unsafe)unsafeField.get(null);
            final Method allocateInstance = sun.misc.Unsafe.class.getDeclaredMethod("allocateInstance", Class.class);
            allocateInstance.setAccessible(true);
            allocateInstance.invoke(u, String.class);
        } catch (
                SecurityException
                | NoSuchFieldException
                | IllegalArgumentException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e) {
            ex = e;
        }
        unsafe = new SunMiscUnsafe(u, ex);
    }

    private SunMiscUnsafe(final sun.misc.Unsafe unsafe, final Exception ex) {
        sunUnsafe = unsafe;
        exception = ex;

    }

    public static Unsafe theInstance() {
        return unsafe;
    }

    @Override
    public Exception getInitException() {
        return exception;
    }

    @Override
    public <T> T allocateInstance(final Class<T> type) throws InstantiationException {
        @SuppressWarnings("unchecked")
        T result = (T)sunUnsafe.allocateInstance(type);
        return result;
    }

    @Override
    public void putByte(final Object object, final long offset, final byte value) {
        sunUnsafe.putByte(object, offset, value);
    }

    @Override
    public void putShort(final Object object, final long offset, final short value) {
        sunUnsafe.putShort(object, offset, value);
    }

    @Override
    public void putInt(final Object object, final long offset, final int value) {
        sunUnsafe.putInt(object, offset, value);
    }

    @Override
    public void putLong(final Object object, final long offset, final long value) {
        sunUnsafe.putLong(object, offset, value);
    }

    @Override
    public void putChar(final Object object, final long offset, final char value) {
        sunUnsafe.putChar(object, offset, value);
    }

    @Override
    public void putBoolean(final Object object, final long offset, final boolean value) {
        sunUnsafe.putBoolean(object, offset, value);
    }

    @Override
    public void putFloat(final Object object, final long offset, final float value) {
        sunUnsafe.putFloat(object, offset, value);
    }

    @Override
    public void putDouble(final Object object, final long offset, final double value) {
        sunUnsafe.putDouble(object, offset, value);
    }

    @Override
    public void putObject(final Object object, final long offset, final Object value) {
        sunUnsafe.putObject(object, offset, value);
    }

    @Override
    public long objectFieldOffset(final Field field) {
        return sunUnsafe.objectFieldOffset(field);
    }
}
