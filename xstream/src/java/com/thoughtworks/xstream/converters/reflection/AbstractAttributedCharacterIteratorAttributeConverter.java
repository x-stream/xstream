/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. February 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * An abstract converter implementation for constants of
 * {@link AttributedCharacterIterator.Attribute} and derived types.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.2
 */
public class AbstractAttributedCharacterIteratorAttributeConverter extends
    AbstractSingleValueConverter {

    private static final Method getName;
    static {
        try {
            getName = AttributedCharacterIterator.Attribute.class.getDeclaredMethod(
                "getName", (Class[])null);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError("Missing AttributedCharacterIterator.Attribute.getName()");
        }
    }

    private final Class type;
    private transient Map attributeMap;
    private transient FieldDictionary fieldDictionary;

    public AbstractAttributedCharacterIteratorAttributeConverter(final Class type) {
        super();
        this.type = type;
        readResolve();
    }

    public boolean canConvert(final Class type) {
        return type == this.type;
    }

    public String toString(final Object source) {
        AttributedCharacterIterator.Attribute attribute = (AttributedCharacterIterator.Attribute)source;
        try {
            if (!getName.isAccessible()) {
                getName.setAccessible(true);
            }
            return (String)getName.invoke(attribute, (Object[])null);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException(
                "Cannot get name of AttributedCharacterIterator.Attribute", e);
        } catch (InvocationTargetException e) {
            throw new ObjectAccessException(
                "Cannot get name of AttributedCharacterIterator.Attribute", e
                    .getTargetException());
        }
    }

    public Object fromString(final String str) {
        return attributeMap.get(str);
    }

    private Object readResolve() {
        fieldDictionary = new FieldDictionary();
        attributeMap = new HashMap();
        for (final Iterator iterator = fieldDictionary.fieldsFor(type); iterator
            .hasNext();) {
            final Field field = (Field)iterator.next();
            if (field.getType() == type && Modifier.isStatic(field.getModifiers())) {
                try {
                    final Object attribute = field.get(null);
                    attributeMap.put(toString(attribute), attribute);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Cannot get object of " + field, e);
                }
            }
        }
        return this;
    }

}
