/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2013, 2014, 2015, 2016, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamConstants;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.ErrorWritingException;
import com.thoughtworks.xstream.core.util.Fields;


/**
 * Pure Java ObjectFactory that instantiates objects using standard Java reflection, however the types of objects that
 * can be constructed are limited.
 * <p>
 * Can newInstance: classes with public visibility, outer classes, static inner classes, classes with default
 * constructors and any class that implements java.io.Serializable.
 * </p>
 * <p>
 * Cannot newInstance: classes without public visibility, non-static inner classes, classes without default
 * constructors. Note that any code in the constructor of a class will be executed when the ObjectFactory instantiates
 * the object.
 * </p>
 *
 * @author Joe Walnes
 */
public class PureJavaReflectionProvider implements ReflectionProvider {

    private transient ConcurrentMap<Class<?>, ObjectStreamClass> objectStreamClassCache;
    private transient ConcurrentMap<Class<?>, byte[]> serializedDataCache;
    protected FieldDictionary fieldDictionary;

    public PureJavaReflectionProvider() {
        this(new FieldDictionary(new ImmutableFieldKeySorter()));
    }

    public PureJavaReflectionProvider(final FieldDictionary fieldDictionary) {
        this.fieldDictionary = fieldDictionary;
        init();
    }

    @Override
    public Object newInstance(final Class<?> type) {
        ErrorWritingException ex = null;
        if (type == void.class || type == Void.class) {
            ex = new ConversionException("Security alert: Marshalling rejected");
        } else {
            try {
                for (final Constructor<?> constructor : type.getDeclaredConstructors()) {
                    if (constructor.getParameterTypes().length == 0) {
                        if (!constructor.isAccessible()) {
                            constructor.setAccessible(true);
                        }
                        return constructor.newInstance(new Object[0]);
                    }
                }
                if (Serializable.class.isAssignableFrom(type)) {
                    return instantiateUsingSerialization(type);
                } else {
                    ex = new ObjectAccessException("Cannot construct type as it does not have a no-args constructor");
                }
            } catch (final InstantiationException | IllegalAccessException e) {
                ex = new ObjectAccessException("Cannot construct type", e);
            } catch (final InvocationTargetException e) {
                if (e.getTargetException() instanceof RuntimeException) {
                    throw (RuntimeException)e.getTargetException();
                } else if (e.getTargetException() instanceof Error) {
                    throw (Error)e.getTargetException();
                } else {
                    ex = new ObjectAccessException("Constructor for type threw an exception", e.getTargetException());
                }
            }
        }
        ex.add("construction-type", type.getName());
        throw ex;
    }

    private Object instantiateUsingSerialization(final Class<?> type) {
        ObjectAccessException oaex = null;
        try {
            if (Reflections.newInstance != null) {
                final ObjectStreamClass osClass = objectStreamClassCache
                    .computeIfAbsent(type, t -> ObjectStreamClass.lookup(type));
                return Reflections.newInstance.invoke(osClass);
            }
            final byte[] data = serializedDataCache.computeIfAbsent(type, t -> {
                final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                final DataOutputStream stream = new DataOutputStream(bytes);
                try {
                    stream.writeShort(ObjectStreamConstants.STREAM_MAGIC);
                    stream.writeShort(ObjectStreamConstants.STREAM_VERSION);
                    stream.writeByte(ObjectStreamConstants.TC_OBJECT);
                    stream.writeByte(ObjectStreamConstants.TC_CLASSDESC);
                    stream.writeUTF(t.getName());
                    stream.writeLong(ObjectStreamClass.lookup(t).getSerialVersionUID());
                    stream.writeByte(2); // classDescFlags (2 = Serializable)
                    stream.writeShort(0); // field count
                    stream.writeByte(ObjectStreamConstants.TC_ENDBLOCKDATA);
                    stream.writeByte(ObjectStreamConstants.TC_NULL);
                } catch (final IOException e) {
                    throw new ObjectAccessException("Cannot prepare data to create type by JDK serialization", e);
                }
                return bytes.toByteArray();
            });

            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data)) {
                @Override
                protected Class<?> resolveClass(final ObjectStreamClass desc) throws ClassNotFoundException {
                    return Class.forName(desc.getName(), false, type.getClassLoader());
                }
            };
            return in.readObject();
        } catch (final ObjectAccessException e) {
            oaex = e;
        } catch (final IOException e) {
            oaex = new ObjectAccessException("Cannot create type by JDK serialization", e);
        } catch (final ClassNotFoundException e) {
            oaex = new ObjectAccessException("Cannot find class", e);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            oaex = new ObjectAccessException("Cannot create type by JDK object stream data", e);
        }
        oaex.add("construction-type", type.getName());
        throw oaex;
    }

    @Override
    public void visitSerializableFields(final Object object, final ReflectionProvider.Visitor visitor) {
        for (final Iterator<Field> iterator = fieldDictionary.fieldsFor(object.getClass()); iterator.hasNext();) {
            final Field field = iterator.next();
            if (!fieldModifiersSupported(field)) {
                continue;
            }
            validateFieldAccess(field);
            final Object value = Fields.read(field, object);
            visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
        }
    }

    @Override
    public void writeField(final Object object, final String fieldName, final Object value, final Class<?> definedIn) {
        final Field field = fieldDictionary.field(object.getClass(), fieldName, definedIn);
        validateFieldAccess(field);
        Fields.write(field, object, value);
    }

    @Override
    public Class<?> getFieldType(final Object object, final String fieldName, final Class<?> definedIn) {
        return fieldDictionary.field(object.getClass(), fieldName, definedIn).getType();
    }

    /**
     * @deprecated As of 1.4.5, use {@link #getFieldOrNull(Class, String)} instead
     */
    @Deprecated
    @Override
    public boolean fieldDefinedInClass(final String fieldName, final Class<?> type) {
        final Field field = fieldDictionary.fieldOrNull(type, fieldName, null);
        return field != null && fieldModifiersSupported(field);
    }

    protected boolean fieldModifiersSupported(final Field field) {
        final int modifiers = field.getModifiers();
        return !(Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers));
    }

    protected void validateFieldAccess(final Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
        }
    }

    @Override
    public Field getField(final Class<?> definedIn, final String fieldName) {
        return fieldDictionary.field(definedIn, fieldName, null);
    }

    @Override
    public Field getFieldOrNull(final Class<?> definedIn, final String fieldName) {
        return fieldDictionary.fieldOrNull(definedIn, fieldName, null);
    }

    public void setFieldDictionary(final FieldDictionary dictionary) {
        fieldDictionary = dictionary;
    }

    private Object readResolve() {
        init();
        return this;
    }

    protected void init() {
        objectStreamClassCache = new ConcurrentHashMap<>();
        serializedDataCache = new ConcurrentHashMap<>();
    }

    private static class Reflections {
        private final static Method newInstance;
        static {
            Method method = null;
            try {
                method = ObjectStreamClass.class.getDeclaredMethod("newInstance");
                method.setAccessible(true);
            } catch (final NoSuchMethodException | SecurityException e) {
                // not available
            }
            newInstance = method;
        }
    }
}
