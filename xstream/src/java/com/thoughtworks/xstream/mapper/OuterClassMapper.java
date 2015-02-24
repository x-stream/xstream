/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 31. January 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.core.Caching;


/**
 * Mapper that uses a more meaningful alias for the field in an inner class (this$0) that refers to the outer class.
 *
 * @author Joe Walnes
 */
public class OuterClassMapper extends MapperWrapper implements Caching {

    private static final String[] EMPTY_NAMES = new String[0];
    private final String alias;
    private final Map innerFields;

    public OuterClassMapper(Mapper wrapped) {
        this(wrapped, "outer-class");
    }

    public OuterClassMapper(Mapper wrapped, String alias) {
        super(wrapped);
        this.alias = alias;
        innerFields = Collections.synchronizedMap(new HashMap());
        innerFields.put(Object.class.getName(), EMPTY_NAMES);
    }

    public String serializedMember(Class type, String memberName) {
        if (memberName.startsWith("this$")) {
            final String[] innerFieldNames = getInnerFieldNames(type);
            for (int i = 0; i < innerFieldNames.length; ++i) {
                if (innerFieldNames[i].equals(memberName)) {
                    return i == 0 ? alias : alias + '-' + i;
                }
            }
        }
        return super.serializedMember(type, memberName);
    }

    public String realMember(Class type, String serialized) {
        if (serialized.startsWith(alias)) {
            int idx = -1;
            final int len = alias.length();
            if (len == serialized.length()) {
                idx = 0;
            } else if (serialized.length() > len + 1 && serialized.charAt(len) == '-') {
                idx = Integer.valueOf(serialized.substring(len + 1)).intValue();
            }
            if (idx >= 0) {
                final String[] innerFieldNames = getInnerFieldNames(type);
                if (idx < innerFieldNames.length) {
                    return innerFieldNames[idx];
                }
            }
        }
        return super.realMember(type, serialized);
    }

    private String[] getInnerFieldNames(final Class type) {
        String[] innerFieldNames = (String[])innerFields.get(type.getName());
        if (innerFieldNames == null) {
            innerFieldNames = getInnerFieldNames(type.getSuperclass());
            Field[] declaredFields = type.getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                final Field field = declaredFields[i];
                if (field.getName().startsWith("this$")) {
                    String[] temp = new String[innerFieldNames.length+1];
                    System.arraycopy(innerFieldNames, 0, temp, 0, innerFieldNames.length);
                    innerFieldNames = temp;
                    innerFieldNames[innerFieldNames.length - 1] = field.getName();
                }
            }
            innerFields.put(type.getName(), innerFieldNames);
        }
        return innerFieldNames;
    }

    public void flushCache() {
        innerFields.keySet().retainAll(Collections.singletonList(Object.class.getName()));
    }
}
