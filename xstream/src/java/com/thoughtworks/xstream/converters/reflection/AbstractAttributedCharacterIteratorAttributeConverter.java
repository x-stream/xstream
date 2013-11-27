/*
 * Copyright (C) 2007, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. February 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.core.util.Fields;

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
 * {@link java.text.AttributedCharacterIterator.Attribute} and derived types.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.2
 */
public class AbstractAttributedCharacterIteratorAttributeConverter extends
    AbstractSingleValueConverter {

    private static final Map instanceMaps = new HashMap();
    private static final Method getName;
    static {
        Method method = null;
        try {
            method = AttributedCharacterIterator.Attribute.class.getDeclaredMethod(
                "getName", (Class[])null);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
        } catch (SecurityException e) {
            // ignore for now
        } catch (NoSuchMethodException e) {
            // ignore for now
        }
        getName = method;
    }

    private final Class type;
    private transient Map attributeMap;

    public AbstractAttributedCharacterIteratorAttributeConverter(final Class type) {
        super();
        if (!AttributedCharacterIterator.Attribute.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName()
                + " is not a " + AttributedCharacterIterator.Attribute.class.getName());
        }
        this.type = type;
        readResolve();
    }

    public boolean canConvert(final Class type) {
        return type == this.type && !attributeMap.isEmpty();
    }

    public String toString(final Object source) {
        return getName((AttributedCharacterIterator.Attribute)source);
    }

    private String getName(AttributedCharacterIterator.Attribute attribute) {
        Exception ex = null;
        if (getName != null) {
            try {
                return (String)getName.invoke(attribute, (Object[])null);
            } catch (IllegalAccessException e) {
                ex = e;
            } catch (InvocationTargetException e) {
                ex = e;
            }
        }
        String s = attribute.toString();
        String className = attribute.getClass().getName();
        if (s.startsWith(className)) {
            return s.substring(className.length()+1, s.length()-1);
        }
        throw new ConversionException("Cannot find name of attribute of type " + className, ex);
    }

    public Object fromString(final String str) {
        if (attributeMap.containsKey(str)) {
            return attributeMap.get(str);
        }
        throw new ConversionException("Cannot find attribute of type " + type.getName() + " with name " + str);
    }

    private Object readResolve() {
        attributeMap = (Map)instanceMaps.get(type.getName());
        if (attributeMap == null) {
            attributeMap = new HashMap();
            Field instanceMap = Fields.locate(type, Map.class, true);
            if (instanceMap != null) {
                try {
                    Map map = (Map)Fields.read(instanceMap, null);
                    if (map != null) {
                        boolean valid = true;
                        for (Iterator iter = map.entrySet().iterator(); valid && iter.hasNext(); ) {
                            Map.Entry entry = (Map.Entry)iter.next(); 
                            valid = entry.getKey().getClass() == String.class && entry.getValue().getClass() == type;
                        }
                        if (valid) {
                            attributeMap.putAll(map);
                        }
                    }
                } catch (ObjectAccessException e) {
                }
            }
            if (attributeMap.isEmpty()) {
                try {
                    Field[] fields = type.getDeclaredFields();
                    for(int i = 0; i < fields.length; ++i) {
                        if(fields[i].getType() == type == Modifier.isStatic(fields[i].getModifiers())) {
                            AttributedCharacterIterator.Attribute attribute =
                                    (AttributedCharacterIterator.Attribute)Fields.read(fields[i], null);
                            attributeMap.put(toString(attribute), attribute);
                        }
                    }
                } catch (SecurityException e) {
                    attributeMap.clear();
                } catch (ObjectAccessException e) {
                    attributeMap.clear();
                } catch (NoClassDefFoundError e) {
                    attributeMap.clear();
                }
            }
            instanceMaps.put(type.getName(), attributeMap);
        }
        return this;
    }

}
