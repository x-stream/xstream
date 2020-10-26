/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.core.util.FastField;


/**
 * Mapper that allows a field of a specific class to be replaced with a shorter alias.
 *
 * @author Joe Walnes
 */
public class FieldAliasingMapper extends MapperWrapper {

    protected final Map<FastField, String> fieldToAliasMap = new HashMap<>();
    protected final Map<FastField, String> aliasToFieldMap = new HashMap<>();
    private final ElementIgnoringMapper elementIgnoringMapper;

    public FieldAliasingMapper(final Mapper wrapped) {
        super(wrapped);
        elementIgnoringMapper = lookupMapperOfType(ElementIgnoringMapper.class);
    }

    public void addFieldAlias(final String alias, final Class<?> type, final String fieldName) {
        fieldToAliasMap.put(key(type, fieldName), alias);
        aliasToFieldMap.put(key(type, alias), fieldName);
    }

    /**
     * @deprecated As of 1.4.9 use {@link ElementIgnoringMapper#addElementsToIgnore(Pattern)}.
     */
    @Deprecated
    public void addFieldsToIgnore(final Pattern pattern) {
        if (elementIgnoringMapper != null) {
            elementIgnoringMapper.addElementsToIgnore(pattern);
        }
    }

    private FastField key(final Class<?> type, final String name) {
        return new FastField(type, name);
    }

    /**
     * @deprecated As of 1.4.9 use {@link ElementIgnoringMapper#omitField(Class, String)}.
     */
    @Deprecated
    public void omitField(final Class<?> definedIn, final String fieldName) {
        if (elementIgnoringMapper != null) {
            elementIgnoringMapper.omitField(definedIn, fieldName);
        }
    }

    @Override
    public String serializedMember(final Class<?> type, final String memberName) {
        final String alias = getMember(type, memberName, fieldToAliasMap);
        if (alias == null) {
            return super.serializedMember(type, memberName);
        } else {
            return alias;
        }
    }

    @Override
    public String realMember(final Class<?> type, final String serialized) {
        final String real = getMember(type, serialized, aliasToFieldMap);
        if (real == null) {
            return super.realMember(type, serialized);
        } else {
            return real;
        }
    }

    private String getMember(final Class<?> type, final String name, final Map<FastField, String> map) {
        String member = null;
        for (Class<?> declaringType = type; member == null
            && declaringType != Object.class
            && declaringType != null; declaringType = declaringType.getSuperclass()) {
            member = map.get(key(declaringType, name));
        }
        return member;
    }
}
