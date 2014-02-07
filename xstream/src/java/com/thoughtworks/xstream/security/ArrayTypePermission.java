/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 09. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

/**
 * Permission for any array type.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class ArrayTypePermission implements TypePermission {
    /**
     * @since 1.4.7
     */
    public static final TypePermission ARRAYS = new ArrayTypePermission();

    @Override
    public boolean allows(Class<?> type) {
        return type != null && type.isArray();
    }

    @Override
    public int hashCode() {
        return 13;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == ArrayTypePermission.class;
    }
}
