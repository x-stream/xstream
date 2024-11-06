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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A store for member information of types.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.21
 */
public class MemberStore<T> {

    private final Map<String, Map<String, T>> types;
    private final boolean synced;

    /**
     * Creates a new instance of a MemberStore.
     *
     * @since 1.4.21
     */
    public static <T> MemberStore<T> newInstance() {
        return new MemberStore<>(false);
    }

    /**
     * Creates a new synchronized instance of a MemberStore.
     *
     * @since 1.4.21
     */
    public static <T> MemberStore<T> newSynchronizedInstance() {
        return new MemberStore<>(true);
    }

    private MemberStore(final boolean synced) {
        this.synced = synced;
        types = synced ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    /**
     * Put an element for a the member of the type into the store.
     *
     * @param definedIn the type owning the member or null
     * @param member the member name
     * @param value the value to store
     * @return the old stored value for the member or null
     * @since 1.4.21
     */
    public Object put(final Class<?> definedIn, final String member, final T value) {
        final String className = definedIn == null ? null : definedIn.getName();
        Map<String, T> store = types.get(className);
        if (store == null) {
            store = synced ? new ConcurrentHashMap<>() : new HashMap<>();
            types.put(className, store);
        }
        return store.put(member, value);
    }

    /**
     * Get the value for a type's member in the store.
     *
     * @param definedIn the type owning the member or null
     * @param member the member name
     * @return the stored value for the member or null
     * @since 1.4.21
     */
    public T get(final Class<?> definedIn, final String member) {
        final String className = definedIn == null ? null : definedIn.getName();
        final Map<String, T> store = types.get(className);
        if (store != null) {
            return store.get(member);
        }
        return null;
    }

    /**
     * Get the set of types in the store.
     *
     * @return the set of type names
     * @since 1.4.21
     */
    public Set<String> keySet() {
        return types.keySet();
    }
}
