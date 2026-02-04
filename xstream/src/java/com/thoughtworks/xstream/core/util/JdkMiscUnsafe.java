/*
 * Copyright (C) 2026 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 3rd February 2026 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.lang.reflect.Field;

import com.thoughtworks.xstream.converters.reflection.Unsafe;


/**
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
class JdkMiscUnsafe implements Unsafe {

    private static final Unsafe unsafe;
    private final jdk.internal.misc.Unsafe jdkUnsafe;
    private final Exception exception;
    static {
        jdk.internal.misc.Unsafe u = null;
        Exception ex = null;
        try {
            u = jdk.internal.misc.Unsafe.getUnsafe();
            u.allocateInstance(String.class);
        } catch (SecurityException | IllegalArgumentException | InstantiationException e) {
            ex = e;
        }
        unsafe = new JdkMiscUnsafe(u, ex);
    }

    private JdkMiscUnsafe(final jdk.internal.misc.Unsafe unsafe, final Exception ex) {
        jdkUnsafe = unsafe;
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
        final T result = (T)jdkUnsafe.allocateInstance(type);
        return result;
    }

    @Override
    public void putByte(final Object object, final long offset, final byte value) {
        jdkUnsafe.putByte(object, offset, value);
    }

    @Override
    public void putShort(final Object object, final long offset, final short value) {
        jdkUnsafe.putShort(object, offset, value);
    }

    @Override
    public void putInt(final Object object, final long offset, final int value) {
        jdkUnsafe.putInt(object, offset, value);
    }

    @Override
    public void putLong(final Object object, final long offset, final long value) {
        jdkUnsafe.putLong(object, offset, value);
    }

    @Override
    public void putChar(final Object object, final long offset, final char value) {
        jdkUnsafe.putChar(object, offset, value);
    }

    @Override
    public void putBoolean(final Object object, final long offset, final boolean value) {
        jdkUnsafe.putBoolean(object, offset, value);
    }

    @Override
    public void putFloat(final Object object, final long offset, final float value) {
        jdkUnsafe.putFloat(object, offset, value);
    }

    @Override
    public void putDouble(final Object object, final long offset, final double value) {
        jdkUnsafe.putDouble(object, offset, value);
    }

    @Override
    public void putObject(final Object object, final long offset, final Object value) {
        jdkUnsafe.putReference(object, offset, value);
    }

    @Override
    public long objectFieldOffset(final Field field) {
        return jdkUnsafe.objectFieldOffset(field);
    }
}
