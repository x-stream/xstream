/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 08. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

/**
 * No permission for any type.
 * <p>
 * Can be used to skip any existing default permission.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class NoTypePermission implements TypePermission {

    /**
     * @since 1.4.7
     */
    public static final TypePermission NONE = new NoTypePermission();

    @Override
    public boolean allows(Class<?> type) {
        throw new ForbiddenClassException(type);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == NoTypePermission.class;
    }
}
