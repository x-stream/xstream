/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 08. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

/**
 * Permission for any type and <code>null</code>.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class AnyTypePermission implements TypePermission {
    /**
     * @since 1.4.7
     */
    public static final TypePermission ANY = new AnyTypePermission();

    public boolean allows(Class type) {
        return true;
    }

    public int hashCode() {
        return 3;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == AnyTypePermission.class;
    }
}
