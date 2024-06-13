/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2013, 2014, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 09. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Mapper that allows a field of a specific class to be replaced with a shorter alias.
 *
 * @author Joe Walnes
 */
public class FieldAliasingMapper extends MapperWrapper {

    protected final Map/*<String, Map<String, String>>*/ fieldToAliasMap = new HashMap();
    protected final Map/*<String, Map<String, String>>*/ aliasToFieldMap = new HashMap();
    private final ElementIgnoringMapper elementIgnoringMapper;

    public FieldAliasingMapper(Mapper wrapped) {
        super(wrapped);
        elementIgnoringMapper = 
            (ElementIgnoringMapper)lookupMapperOfType(ElementIgnoringMapper.class);
    }

    public void addFieldAlias(String alias, Class type, String fieldName) {
        String name = type == null ? null : type.getName();
        put(fieldToAliasMap, name, fieldName, alias);
        put(aliasToFieldMap, name, alias, fieldName);
    }

    private void put(Map map1, String k1, String k2, String v) {
        Map map2 = (Map) map1.get(k1);
        if (map2 == null) {
            map2 = new HashMap();
            map1.put(k1, map2);
        }
        map2.put(k2, v);
    }

    /**
     * @deprecated As of 1.4.9 use {@link ElementIgnoringMapper#addElementsToIgnore(Pattern)}.
     */
    public void addFieldsToIgnore(final Pattern pattern) {
        if (elementIgnoringMapper != null) {
            elementIgnoringMapper.addElementsToIgnore(pattern);
        }
    }

    /**
     * @deprecated As of 1.4.9 use {@link ElementIgnoringMapper#omitField(Class, String)}.
     */
    public void omitField(Class definedIn, String fieldName) {
        if (elementIgnoringMapper != null) {
            elementIgnoringMapper.omitField(definedIn, fieldName);
        }
    }

    public String serializedMember(Class type, String memberName) {
        String alias = getMember(type, memberName, fieldToAliasMap);
        if (alias == null) {
            return super.serializedMember(type, memberName);
        } else {
            return alias;
        }
    }

    public String realMember(Class type, String serialized) {
        String real = getMember(type, serialized, aliasToFieldMap);
        if (real == null) {
            return super.realMember(type, serialized);
        } else {
            return real;
        }
    }

    private String getMember(Class type, String name, Map map) {
        for (Class declaringType = type;
             declaringType != Object.class && declaringType != null;
                declaringType = declaringType.getSuperclass()) {
            Map classMap = (Map) map.get(declaringType.getName());
            if (classMap != null) {
                String member = (String) classMap.get(name);
                if (member != null) {
                    return member;
                }
            }
        }
        return null;
    }
}
