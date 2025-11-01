/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2011, 2013, 2014, 2015, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 08. January 2014 by Joerg Schaible, renamed from Sun14ReflectionProvider
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Instantiates a new object bypassing the constructor using undocumented internal JDK features.
 * <p>
 * The code in the constructor will never be executed and parameters do not have to be known. This is the same method
 * used by the internals of standard Java serialization, but relies on internal code (jdk.internal.misc.Unsafe).
 * <p>
 * <p>
 * The implementation will use the same internals to write into fields. This is a lot faster and was additionally the
 * only possibility to set final fields prior to Java 5.
 * <p>
 *
 * @author Joe Walnes
 * @author Brian Slesinsky
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class SunUnsafeReflectionProvider extends SunLimitedUnsafeReflectionProvider {

    // references to the Field key are kept in the FieldDictionary
    private transient ConcurrentMap<Field, Long> fieldOffsetCache;

    /**
     * @since 1.4.7
     */
    public SunUnsafeReflectionProvider() {
        super();
    }

    /**
     * @since 1.4.7
     */
    public SunUnsafeReflectionProvider(final FieldDictionary dic) {
        super(dic);
    }

    @Override
    public void writeField(final Object object, final String fieldName, final Object value, final Class<?> definedIn) {
        write(fieldDictionary.field(object.getClass(), fieldName, definedIn), object, value);
    }

    private void write(final Field field, final Object object, final Object value) {
        if (exception != null) {
            final ObjectAccessException ex = new ObjectAccessException("Cannot set field", exception);
            ex.add("field", object.getClass() + "." + field.getName());
            throw ex;
        }
        try {
            final long offset = getFieldOffset(field);
            final Class<?> type = field.getType();
            if (type.isPrimitive()) {
                if (type.equals(Integer.TYPE)) {
                    unsafe.putInt(object, offset, ((Integer)value).intValue());
                } else if (type.equals(Long.TYPE)) {
                    unsafe.putLong(object, offset, ((Long)value).longValue());
                } else if (type.equals(Short.TYPE)) {
                    unsafe.putShort(object, offset, ((Short)value).shortValue());
                } else if (type.equals(Character.TYPE)) {
                    unsafe.putChar(object, offset, ((Character)value).charValue());
                } else if (type.equals(Byte.TYPE)) {
                    unsafe.putByte(object, offset, ((Byte)value).byteValue());
                } else if (type.equals(Float.TYPE)) {
                    unsafe.putFloat(object, offset, ((Float)value).floatValue());
                } else if (type.equals(Double.TYPE)) {
                    unsafe.putDouble(object, offset, ((Double)value).doubleValue());
                } else if (type.equals(Boolean.TYPE)) {
                    unsafe.putBoolean(object, offset, ((Boolean)value).booleanValue());
                } else {
                    final ObjectAccessException ex = new ObjectAccessException("Cannot set field of unknown type",
                        exception);
                    ex.add("field", object.getClass() + "." + field.getName());
                    ex.add("unknown-type", type.getName());
                    throw ex;
                }
            } else {
                unsafe.putObject(object, offset, value);
            }

        } catch (final IllegalArgumentException e) {
            final ObjectAccessException ex = new ObjectAccessException("Cannot set field", e);
            ex.add("field", object.getClass() + "." + field.getName());
            throw ex;
        }
    }

    private long getFieldOffset(final Field f) {
        Long l = fieldOffsetCache.get(f);
        if (l == null) {
            fieldOffsetCache.putIfAbsent(f, Long.valueOf(unsafe.objectFieldOffset(f)));
            l = fieldOffsetCache.get(f);
        }

        return l.longValue();
    }

    private Object readResolve() {
        init();
        return this;
    }

    @Override
    protected void init() {
        super.init();
        fieldOffsetCache = new ConcurrentHashMap<>();
    }
}
