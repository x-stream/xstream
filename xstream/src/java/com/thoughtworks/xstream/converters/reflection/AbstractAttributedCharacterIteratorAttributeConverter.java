/*
 * Copyright (C) 2007, 2013, 2014, 2015, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 01. February 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.core.util.Fields;


/**
 * An abstract converter implementation for constants of {@link java.text.AttributedCharacterIterator.Attribute} and
 * derived types.
 *
 * @author J&ouml;rg Schaible
 * @since 1.2.2
 */
public class AbstractAttributedCharacterIteratorAttributeConverter<T extends AttributedCharacterIterator.Attribute>
    extends AbstractSingleValueConverter {

    private static final Map<String, Map<String, ? extends AttributedCharacterIterator.Attribute>> instanceMaps =
            new HashMap<>();
    private static final Method getName;

    static {
        Method method = null;
        try {
            method = AttributedCharacterIterator.Attribute.class.getDeclaredMethod("getName", (Class[])null);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
        } catch (final SecurityException e) {
            // ignore for now
        } catch (final NoSuchMethodException e) {
            // ignore for now
        }
        getName = method;
    }

    private final Class<? extends T> type;
    private transient Map<String, T> attributeMap;

    public AbstractAttributedCharacterIteratorAttributeConverter(final Class<? extends T> type) {
        super();
        if (!AttributedCharacterIterator.Attribute.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName()
                + " is not a "
                + AttributedCharacterIterator.Attribute.class.getName());
        }
        this.type = type;
        readResolve();
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == this.type && !attributeMap.isEmpty();
    }

    @Override
    public String toString(final Object source) {
        @SuppressWarnings("unchecked")
        final T t = (T)source;
        return getName(t);
    }

    private String getName(final AttributedCharacterIterator.Attribute attribute) {
        Exception ex = null;
        if (getName != null) {
            try {
                return (String)getName.invoke(attribute);
            } catch (final IllegalAccessException e) {
                ex = e;
            } catch (final InvocationTargetException e) {
                ex = e;
            }
        }
        final String s = attribute.toString();
        final String className = attribute.getClass().getName();
        if (s.startsWith(className)) {
            return s.substring(className.length() + 1, s.length() - 1);
        }
        final ConversionException exception = new ConversionException("Cannot find name of attribute", ex);
        exception.add("attribute-type", className);
        throw exception;
    }

    @Override
    public Object fromString(final String str) {
        if (attributeMap.containsKey(str)) {
            return attributeMap.get(str);
        }
        final ConversionException exception = new ConversionException("Cannot find attribute");
        exception.add("attribute-type", type.getName());
        exception.add("attribute-name", str);
        throw exception;
    }

    private Object readResolve() {
        @SuppressWarnings("unchecked")
        final Map<String, T> typedMap = (Map<String, T>)instanceMaps.get(type.getName());
        attributeMap = typedMap;
        if (attributeMap == null) {
            attributeMap = new HashMap<>();
            final Field instanceMap = Fields.locate(type, Map.class, true);
            if (instanceMap != null) {
                try {
                    @SuppressWarnings("unchecked")
                    final Map<String, T> map = (Map<String, T>)Fields.read(instanceMap, null);
                    if (map != null) {
                        boolean valid = true;
                        for (final Map.Entry<String, T> entry : map.entrySet()) {
                            valid = entry.getKey().getClass() == String.class && entry.getValue().getClass() == type;
                        }
                        if (valid) {
                            attributeMap.putAll(map);
                        }
                    }
                } catch (final ObjectAccessException e) {
                }
            }
            if (attributeMap.isEmpty()) {
                try {
                    final Field[] fields = type.getDeclaredFields();
                    for (final Field field : fields) {
                        if (field.getType() == type == Modifier.isStatic(field.getModifiers())) {
                            @SuppressWarnings("unchecked")
                            final T attribute = (T)Fields.read(field, null);
                            attributeMap.put(toString(attribute), attribute);
                        }
                    }
                } catch (final SecurityException e) {
                    attributeMap.clear();
                } catch (final ObjectAccessException e) {
                    attributeMap.clear();
                } catch (final NoClassDefFoundError e) {
                    attributeMap.clear();
                }
            }
            instanceMaps.put(type.getName(), attributeMap);
        }
        return this;
    }

}
