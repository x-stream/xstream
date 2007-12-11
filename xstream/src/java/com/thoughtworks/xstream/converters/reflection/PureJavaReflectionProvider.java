/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.core.JVM;

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
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Pure Java ObjectFactory that instantiates objects using standard Java reflection, however the types of objects
 * that can be constructed are limited.
 * <p/>
 * Can newInstance: classes with public visibility, outer classes, static inner classes, classes with default constructors
 * and any class that implements java.io.Serializable.
 * Cannot newInstance: classes without public visibility, non-static inner classes, classes without default constructors.
 * Note that any code in the constructor of a class will be executed when the ObjectFactory instantiates the object.
 * </p>
 * @author Joe Walnes
 */
public class PureJavaReflectionProvider implements ReflectionProvider {

    private transient Map serializedDataCache = Collections.synchronizedMap(new HashMap());
    protected FieldDictionary fieldDictionary;

	public PureJavaReflectionProvider() {
		this(new FieldDictionary(new ImmutableFieldKeySorter()));
	}

	public PureJavaReflectionProvider(FieldDictionary fieldDictionary) {
		this.fieldDictionary = fieldDictionary;
	}

	public Object newInstance(Class type) {
        try {
            Constructor[] constructors = type.getDeclaredConstructors();
            for (int i = 0; i < constructors.length; i++) {
                if (constructors[i].getParameterTypes().length == 0) {
                    if (!Modifier.isPublic(constructors[i].getModifiers())) {
                        constructors[i].setAccessible(true);
                    }
                    return constructors[i].newInstance(new Object[0]);
                }
            }
            if (Serializable.class.isAssignableFrom(type)) {
                return instantiateUsingSerialization(type);
            } else {
                throw new ObjectAccessException("Cannot construct " + type.getName()
                        + " as it does not have a no-args constructor");
            }
        } catch (InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)e.getTargetException();
            } else if (e.getTargetException() instanceof Error) {
                throw (Error)e.getTargetException();
            } else {
                throw new ObjectAccessException("Constructor for " + type.getName() + " threw an exception", e.getTargetException());
            }
        }
    }

    private Object instantiateUsingSerialization(Class type) {
        try {
            byte[] data;
            if (serializedDataCache.containsKey(type)) {
                data = (byte[]) serializedDataCache.get(type);
            } else {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream stream = new DataOutputStream(bytes);
                stream.writeShort(ObjectStreamConstants.STREAM_MAGIC);
                stream.writeShort(ObjectStreamConstants.STREAM_VERSION);
                stream.writeByte(ObjectStreamConstants.TC_OBJECT);
                stream.writeByte(ObjectStreamConstants.TC_CLASSDESC);
                stream.writeUTF(type.getName());
                stream.writeLong(ObjectStreamClass.lookup(type).getSerialVersionUID());
                stream.writeByte(2);  // classDescFlags (2 = Serializable)
                stream.writeShort(0); // field count
                stream.writeByte(ObjectStreamConstants.TC_ENDBLOCKDATA);
                stream.writeByte(ObjectStreamConstants.TC_NULL);
                data = bytes.toByteArray();
                serializedDataCache.put(type, data);
            }

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
            return in.readObject();
        } catch (IOException e) {
            throw new ObjectAccessException("Cannot create " + type.getName() + " by JDK serialization", e);
        } catch (ClassNotFoundException e) {
            throw new ObjectAccessException("Cannot find class " + e.getMessage());
        }
    }

    public void visitSerializableFields(Object object, ReflectionProvider.Visitor visitor) {
        for (Iterator iterator = fieldDictionary.fieldsFor(object.getClass()); iterator.hasNext();) {
            Field field = (Field) iterator.next();
            if (!fieldModifiersSupported(field)) {
                continue;
            }
            validateFieldAccess(field);
            try {
                Object value = field.get(object);
                visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
            } catch (IllegalArgumentException e) {
                throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
            } catch (IllegalAccessException e) {
                throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
            }
        }
    }

    public void writeField(Object object, String fieldName, Object value, Class definedIn) {
        Field field = fieldDictionary.field(object.getClass(), fieldName, definedIn);
        validateFieldAccess(field);
        try {
            field.set(object, value);
        } catch (IllegalArgumentException e) {
            throw new ObjectAccessException("Could not set field " + object.getClass() + "." + field.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Could not set field " + object.getClass() + "." + field.getName(), e);
        }
    }

    public Class getFieldType(Object object, String fieldName, Class definedIn) {
        return fieldDictionary.field(object.getClass(), fieldName, definedIn).getType();
    }

    public boolean fieldDefinedInClass(String fieldName, Class type) {
        try {
            Field field = fieldDictionary.field(type, fieldName, null);
            return fieldModifiersSupported(field) || Modifier.isTransient(field.getModifiers());
        } catch (ObjectAccessException e) {
            return false;
        }
    }

    protected boolean fieldModifiersSupported(Field field) {
        return !(Modifier.isStatic(field.getModifiers())
                || Modifier.isTransient(field.getModifiers()));
    }

    protected void validateFieldAccess(Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            if (JVM.is15()) {
                field.setAccessible(true);
            } else {
                throw new ObjectAccessException("Invalid final field "
                        + field.getDeclaringClass().getName() + "." + field.getName());
            }
        }
    }

    public Field getField(Class definedIn, String fieldName) {
        return fieldDictionary.field(definedIn, fieldName, null);
    }

    public void setFieldDictionary(FieldDictionary dictionary) {
        this.fieldDictionary = dictionary;
    }

    protected Object readResolve() {
        serializedDataCache = Collections.synchronizedMap(new HashMap());
        return this;
    }

}
