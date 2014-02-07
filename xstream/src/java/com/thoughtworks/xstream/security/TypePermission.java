/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 08. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

/**
 * Definition of a type permission. 
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public interface TypePermission {
    /**
     * Check permission for a provided type.
     * 
     * @param type the type to check
     * @return <code>true</code> if provided type is allowed, <code>false</code> if permission does not handle the type
     * @throws ForbiddenClassException if provided type is explicitly forbidden
     * @since 1.4.7
     */
    boolean allows(Class type);
}
