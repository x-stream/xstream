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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A store for member information of types.
 * 
 * @author J&ouml;rg Schaible
 *
 * @since 1.4.21
 */
public class MemberStore {

    private final Map types;
    private final boolean synced;

    /**
     * Creates a new instance of a MemberStore.
     * 
     * @since 1.4.21
     */
    public static MemberStore newInstance() {
        return new MemberStore(false);
    }

    /**
     * Creates a new synchronized instance of a MemberStore.
     * 
     * @since 1.4.21
     */
    public static MemberStore newSynchronizedInstance() {
        return new MemberStore(true);
    }
    
    private MemberStore(boolean synced) {
        this.synced = synced;
        types = synced ? Collections.synchronizedMap(new HashMap()) : new HashMap();
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
    public Object put(final Class definedIn, final String member, final Object value) {
        final String className = definedIn == null ? null : definedIn.getName();
        Map store = (Map) types.get(className);
        if (store == null) {
            store = new HashMap();
            if (synced) {
                store = Collections.synchronizedMap(store);
            }
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
    public Object get(final Class definedIn, final String member) {
        final String className = definedIn == null ? null : definedIn.getName();
        final Map store = (Map) types.get(className);
        if (store != null)
            return store.get(member);
        return null;
    }
    
    /**
     * Get the set of types in the store.
     * 
     * @return the set of type names
     * @since 1.4.21
     */
    public Set keySet() {
        return types.keySet();
    }
}
