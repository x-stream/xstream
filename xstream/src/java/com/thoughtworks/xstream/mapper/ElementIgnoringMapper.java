/*
 * Copyright (C) 2013, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. January 2016 by Joerg Schaible, factored out from FieldAliasingMapper.
 */
package com.thoughtworks.xstream.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Mapper that allows an field of a specific class to be omitted entirely.
 *
 * @author Joerg Schaible
 */
public class ElementIgnoringMapper extends MapperWrapper {

    protected final Map/*<String, Set<String>>*/ fieldsToOmit = new HashMap();
    protected final Set/*<Pattern>*/ unknownElementsToIgnore = new LinkedHashSet();

    public ElementIgnoringMapper(Mapper wrapped) {
        super(wrapped);
    }
    
    public void addElementsToIgnore(final Pattern pattern) {
        unknownElementsToIgnore.add(pattern);
    }

    public void omitField(Class definedIn, String fieldName) {
        String definedInName = definedIn == null ? null : definedIn.getName();
        Set omitFields = (Set) fieldsToOmit.get(definedInName);
        if (omitFields == null) {
            omitFields = new HashSet();
            fieldsToOmit.put(definedInName, omitFields);
        }
        omitFields.add(fieldName);
    }

    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
        String definedInName = definedIn == null ? null : definedIn.getName();
        Set omitFields = (Set) fieldsToOmit.get(definedInName);
        if (omitFields != null && omitFields.contains(fieldName)) {
            return false;
        } else if (definedIn == Object.class && isIgnoredElement(fieldName)) {
            return false;
        }
        return super.shouldSerializeMember(definedIn, fieldName);
    }

    public boolean isIgnoredElement(String name) {
        if (!unknownElementsToIgnore.isEmpty()) {
            for(Iterator iter = unknownElementsToIgnore.iterator(); iter.hasNext();) {
                Pattern pattern = (Pattern)iter.next();
                if (pattern.matcher(name).matches()) {
                    return true;
                }
            }
        }
        return super.isIgnoredElement(name);
    }
}
