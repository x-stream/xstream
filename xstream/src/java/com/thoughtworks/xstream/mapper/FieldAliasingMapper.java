/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 09. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Mapper that allows a field of a specific class to be replaced with a shorter alias, or omitted
 * entirely.
 *
 * @author Joe Walnes
 */
public class FieldAliasingMapper extends MapperWrapper {

    protected final Map fieldToAliasMap = new HashMap();
    protected final Map aliasToFieldMap = new HashMap();
    protected final Set fieldsToOmit = new HashSet();

    public FieldAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated As of 1.2, use {@link #FieldAliasingMapper(Mapper)}
     */
    public FieldAliasingMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    public void addFieldAlias(String alias, Class type, String fieldName) {
        fieldToAliasMap.put(key(type, fieldName), alias);
        aliasToFieldMap.put(key(type, alias), fieldName);
    }

    private Object key(Class type, String name) {
        return type.getName() + ':' + name;
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
        String member = null;
        for (Class declaringType = type; member == null && declaringType != Object.class; declaringType = declaringType.getSuperclass()) {
            member = (String) map.get(key(declaringType, name));
        }
        return member;
    }

    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
        return !fieldsToOmit.contains(key(definedIn, fieldName));
    }

    public void omitField(Class definedIn, String fieldName) {
        fieldsToOmit.add(key(definedIn, fieldName));
    }
}
