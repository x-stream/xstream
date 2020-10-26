/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2014, 2015 XStream Committers.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.thoughtworks.xstream.core.Caching;


/**
 * Mapper that uses a more meaningful alias for the field in an inner class (this$0) that refers to the outer class.
 *
 * @author Joe Walnes
 */
public class OuterClassMapper extends MapperWrapper implements Caching {

    private static final String[] EMPTY_NAMES = new String[0];
    private final String alias;
    private final ConcurrentMap<String, String[]> innerFields;

    public OuterClassMapper(final Mapper wrapped) {
        this(wrapped, "outer-class");
    }

    public OuterClassMapper(final Mapper wrapped, final String alias) {
        super(wrapped);
        this.alias = alias;
        innerFields = new ConcurrentHashMap<>();
        innerFields.put(Object.class.getName(), EMPTY_NAMES);
    }

    @Override
    public String serializedMember(final Class<?> type, final String memberName) {
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

    @Override
    public String realMember(final Class<?> type, final String serialized) {
        if (serialized.startsWith(alias)) {
            int idx = -1;
            final int len = alias.length();
            if (len == serialized.length()) {
                idx = 0;
            } else if (serialized.length() > len + 1 && serialized.charAt(len) == '-') {
                idx = Integer.valueOf(serialized.substring(len + 1));
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

    private String[] getInnerFieldNames(final Class<?> type) {
        String[] innerFieldNames = innerFields.get(type.getName());
        if (innerFieldNames == null) {
            innerFieldNames = getInnerFieldNames(type.getSuperclass());
            for (final Field field : type.getDeclaredFields()) {
                if (field.getName().startsWith("this$")) {
                    innerFieldNames = Arrays.copyOf(innerFieldNames, innerFieldNames.length + 1);
                    innerFieldNames[innerFieldNames.length - 1] = field.getName();
                }
            }
            innerFields.putIfAbsent(type.getName(), innerFieldNames);
        }
        return innerFieldNames;
    }

    @Override
    public void flushCache() {
        innerFields.keySet().retainAll(Collections.singletonList(Object.class.getName()));
    }
}
