/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2011, 2013, 2014, 2016, 2017, 2020 XStream Committers.
 * All rights reserved.
 *
 * Created on 08. January 2014 by Joerg Schaible, factored out from SunUnsafeReflectionProvider
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.ErrorWritingException;

import sun.misc.Unsafe;


/**
 * Instantiates a new object bypassing the constructor using undocumented internal JDK features.
 * <p>
 * The code in the constructor will never be executed and parameters do not have to be known. This is the same method
 * used by the internals of standard Java serialization, but relies on internal code (sun.misc.Unsafe) that may not be
 * present on all JVMs.
 * <p>
 * <p>
 * The implementation will use standard Java functionality to write any fields. This requires Java 5 as minimum runtime
 * and is used as fallback on platforms that do not provide the complete implementation level for the internals (like
 * Dalvik).
 * <p>
 * 
 * @author J&ouml;rg Schaible
 * @author Joe Walnes
 * @author Brian Slesinsky
 * @since 1.4.7
 */
public class SunLimitedUnsafeReflectionProvider extends PureJavaReflectionProvider {

    protected static final Unsafe unsafe;
    protected static final Exception exception;
    static {
        Unsafe u = null;
        Exception ex = null;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            u = (Unsafe)unsafeField.get(null);
        } catch (SecurityException e) {
            ex = e;
        } catch (NoSuchFieldException e) {
            ex = e;
        } catch (IllegalArgumentException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        }
        exception = ex;
        unsafe = u;
    }

    /**
     * @since 1.4.7
     */
    public SunLimitedUnsafeReflectionProvider() {
        super();
    }

    /**
     * @since 1.4.7
     */
    public SunLimitedUnsafeReflectionProvider(FieldDictionary fieldDictionary) {
        super(fieldDictionary);
    }

    public Object newInstance(Class type) {
        if (exception != null) {
            ObjectAccessException ex = new ObjectAccessException("Cannot construct type", exception);
            ex.add("construction-type", type.getName());
            throw ex;
        }
        ErrorWritingException ex = null;
        if (type == void.class || type == Void.class) {
            ex = new ConversionException("Security alert: Marshalling rejected");
        } else {
            try {
                return unsafe.allocateInstance(type);
            } catch (final SecurityException e) {
                ex = new ObjectAccessException("Cannot construct type", e);
            } catch (final InstantiationException e) {
                ex = new ConversionException("Cannot construct type", e);
            } catch (final IllegalArgumentException e) {
                ex = new ObjectAccessException("Cannot construct type", e);
            }
        }
        ex.add("construction-type", type.getName());
        throw ex;
    }

    protected void validateFieldAccess(Field field) {
        // (overriden) don't mind final fields.
    }

    private Object readResolve() {
        init();
        return this;
    }
}
