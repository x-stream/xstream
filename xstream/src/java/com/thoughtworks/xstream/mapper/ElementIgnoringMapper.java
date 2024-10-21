/*
 * Copyright (C) 2013, 2016, 2024 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. January 2016 by Joerg Schaible, factored out from FieldAliasingMapper.
 */
package com.thoughtworks.xstream.mapper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.core.util.MemberDictionary;

/**
 * Mapper that allows an field of a specific class to be omitted entirely.
 *
 * @author Joerg Schaible
 */
public class ElementIgnoringMapper extends MapperWrapper {

    protected final MemberDictionary fieldsToOmit = new MemberDictionary();
    protected final Map unknownElementsToIgnore = new LinkedHashMap();

    public ElementIgnoringMapper(Mapper wrapped) {
        super(wrapped);
    }
    
    public void addElementsToIgnore(final Pattern pattern) {
        unknownElementsToIgnore.put(pattern.pattern(), pattern);
    }

    public void omitField(Class definedIn, String fieldName) {
        fieldsToOmit.add(definedIn, fieldName);
    }

    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
        if (fieldsToOmit.contains(definedIn, fieldName)) {
            return false;
        } else if (definedIn == Object.class && isIgnoredElement(fieldName)) {
            return false;
        }
        return super.shouldSerializeMember(definedIn, fieldName);
    }

    public boolean isIgnoredElement(String name) {
        if (!unknownElementsToIgnore.isEmpty()) {
            for(Iterator iter = unknownElementsToIgnore.values().iterator(); iter.hasNext();) {
                Pattern pattern = (Pattern)iter.next();
                if (pattern.matcher(name).matches()) {
                    return true;
                }
            }
        }
        return super.isIgnoredElement(name);
    }
}
