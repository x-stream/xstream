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
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;

/**
 * Interface for the different Unsafe.
 * 
 *  XStream uses several methods normally used by the Java serialization only of the Unsafe implementation. The
 *  interface proxies the required methods available in different implementations depending on the Java runtime.
 *
 * @since upcoming
 */
public interface Unsafe {
    public <T> T allocateInstance(final Class<T> type) throws InstantiationException;
    void putByte(Object object, long offset, byte value);
    void putShort(Object object, long offset, short value);
    void putInt(Object object, long offset, int value);
    void putLong(Object object, long offset, long value);
    void putChar(Object object, long offset, char value);
    void putBoolean(Object object, long offset, boolean value);
    void putFloat(Object object, long offset, float value);
    void putDouble(Object object, long offset, double value);
    void putObject(Object object, long offset, Object value);
    long objectFieldOffset(Field field);
    Exception getInitException();
}
