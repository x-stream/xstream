/*
 * Copyright (C) 2024 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 21. October 2024 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * A dictionary for member information of types.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.21
 */
public class MemberDictionary {

    private final Map<String, Set<String>> types;

    /**
     * Constructs an instance.
     *
     * @since 1.4.21
     */
    public MemberDictionary() {
        types = new HashMap<>();
    }

    /**
     * Add the member of the type into the dictionary.
     *
     * @param definedIn the type owning the member or null
     * @param member the member name
     * @return true if the member has been added to the dictionary
     * @since 1.4.21
     */
    public boolean add(final Class<?> definedIn, final String member) {
        final String className = definedIn == null ? null : definedIn.getName();
        Set<String> members = types.get(className);
        if (members == null) {
            members = new HashSet<>();
            types.put(className, members);
        }
        return members.add(member);
    }

    /**
     * Checks the existence of the member of a type in the dictionary.
     *
     * @param definedIn the type owning the member or null
     * @param member the member name
     * @return true if the member is in the dictionary
     * @since 1.4.21
     */
    public boolean contains(final Class<?> definedIn, final String member) {
        final String className = definedIn == null ? null : definedIn.getName();
        final Set<String> members = types.get(className);
        return members != null && members.contains(member);
    }
}
