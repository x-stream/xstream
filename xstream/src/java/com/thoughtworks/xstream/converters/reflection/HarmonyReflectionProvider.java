/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 09. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import org.apache.harmony.awt.FieldsAccessor;
import org.apache.harmony.misc.accessors.ObjectAccessor;

import java.lang.reflect.Field;


/**
 * Instantiates a new object on the Harmony JVM by bypassing the constructor (meaning code in
 * the constructor will never be executed and parameters do not have to be known). This is the
 * same method used by the internals of standard Java serialization, but relies on internal Harmony
 * code.
 * 
 * Note, this is work in progress. Harmony 5.0M4 crashes after running quite some test of the test 
 * suite. Additionally it fails with a NPE processing the annotations and has a wrong offset dealing 
 * with time zone.
 *
 * @author J&ouml;rg Schaible
 * @author Joe Walnes
 */
public class HarmonyReflectionProvider extends PureJavaReflectionProvider {
    private final static ObjectAccessor objectAccess;
    private final static Exception exception;
    static {
        ObjectAccessor accessor = null;
        Exception ex = null;
        FieldsAccessor fieldsAccessor = new FieldsAccessor(ReflectionProvider.class, null);
        Field field;
        try {
            field = FieldsAccessor.class.getDeclaredField("accessor");
            field.setAccessible(true);
            accessor = (ObjectAccessor)field.get(fieldsAccessor);
        } catch (NoSuchFieldException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        }
        objectAccess = accessor;
        exception = ex;
    }

    public HarmonyReflectionProvider() {
        super();
    }

    public HarmonyReflectionProvider(FieldDictionary dic) {
        super(dic);
    }

    public Object newInstance(Class type) {
        if (exception != null) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), exception);
        }
        try {
            return objectAccess.newInstance(type);
        } catch (SecurityException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (IllegalArgumentException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        }
    }

    public void writeField(Object object, String fieldName, Object value, Class definedIn) {
        write(fieldDictionary.field(object.getClass(), fieldName, definedIn), object, value);
    }

    private void write(Field field, Object object, Object value) {
        if (exception != null) {
            throw new ObjectAccessException("Could not set field "
                + object.getClass()
                + "."
                + field.getName(), exception);
        }
        try {
            long offset = objectAccess.getFieldID(field);
            Class type = field.getType();
            if (type.isPrimitive()) {
                if (type.equals(Integer.TYPE)) {
                    objectAccess.setInt(object, offset, ((Integer)value).intValue());
                } else if (type.equals(Long.TYPE)) {
                    objectAccess.setLong(object, offset, ((Long)value).longValue());
                } else if (type.equals(Short.TYPE)) {
                    objectAccess.setShort(object, offset, ((Short)value).shortValue());
                } else if (type.equals(Character.TYPE)) {
                    objectAccess.setChar(object, offset, ((Character)value).charValue());
                } else if (type.equals(Byte.TYPE)) {
                    objectAccess.setByte(object, offset, ((Byte)value).byteValue());
                } else if (type.equals(Float.TYPE)) {
                    objectAccess.setFloat(object, offset, ((Float)value).floatValue());
                } else if (type.equals(Double.TYPE)) {
                    objectAccess.setDouble(object, offset, ((Double)value).doubleValue());
                } else if (type.equals(Boolean.TYPE)) {
                    objectAccess.setBoolean(object, offset, ((Boolean)value).booleanValue());
                } else {
                    throw new ObjectAccessException("Could not set field "
                        + object.getClass()
                        + "."
                        + field.getName()
                        + ": Unknown type "
                        + type);
                }
            } else {
                objectAccess.setObject(object, offset, value);
            }

        } catch (IllegalArgumentException e) {
            throw new ObjectAccessException("Could not set field "
                + object.getClass()
                + "."
                + field.getName(), e);
        }
    }

    protected void validateFieldAccess(Field field) {
        // (overriden) don't mind final fields.
    }
}
