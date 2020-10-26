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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.core.util.FastField;


/**
 * Mapper that allows an field of a specific class to be omitted entirely.
 *
 * @author Joerg Schaible
 */
public class ElementIgnoringMapper extends MapperWrapper {

    protected final Set<Pattern> unknownElementsToIgnore = new LinkedHashSet<>();
    protected final Set<FastField> fieldsToOmit = new HashSet<>();

    public ElementIgnoringMapper(final Mapper wrapped) {
        super(wrapped);
    }

    public void addElementsToIgnore(final Pattern pattern) {
        unknownElementsToIgnore.add(pattern);
    }

    public void omitField(final Class<?> definedIn, final String fieldName) {
        fieldsToOmit.add(key(definedIn, fieldName));
    }

    @Override
    public boolean shouldSerializeMember(final Class<?> definedIn, final String fieldName) {
        if (fieldsToOmit.contains(key(definedIn, fieldName))) {
            return false;
        } else if (definedIn == Object.class && isIgnoredElement(fieldName)) {
            return false;
        }
        return super.shouldSerializeMember(definedIn, fieldName);
    }

    @Override
    public boolean isIgnoredElement(final String name) {
        if (!unknownElementsToIgnore.isEmpty()) {
            for (final Pattern pattern : unknownElementsToIgnore) {
                if (pattern.matcher(name).matches()) {
                    return true;
                }
            }
        }
        return super.isIgnoredElement(name);
    }

    private FastField key(final Class<?> type, final String name) {
        return new FastField(type, name);
    }
}
