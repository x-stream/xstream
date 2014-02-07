/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 09. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

/**
 * Wrapper to negate another type permission.
 * <p>
 * If the wrapped {@link TypePermission} allows the type, this instance will throw a {@link ForbiddenClassException}
 * instead. An instance of this permission cannot be used to allow a type.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class NoPermission implements TypePermission {

    private final TypePermission permission;

    /**
     * Construct a NoPermission.
     * 
     * @param permission the permission to negate or <code>null</code> to forbid any type
     * @since 1.4.7
     */
    public NoPermission(final TypePermission permission) {
        this.permission = permission;
    }

    public boolean allows(final Class type) {
        if (permission == null || permission.allows(type)) {
            throw new ForbiddenClassException(type);
        }
        return false;
    }
}
