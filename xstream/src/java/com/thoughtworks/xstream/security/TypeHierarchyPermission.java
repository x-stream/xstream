/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 23. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

/**
 * Permission for a type hierarchy with a name matching one in the provided list.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class TypeHierarchyPermission implements TypePermission {

    private Class type;

    /**
     * @since 1.4.7
     */
    public TypeHierarchyPermission(Class type) {
        this.type = type;
    }

    public boolean allows(Class type) {
        if (type == null)
            return false;
        return this.type.isAssignableFrom(type);
    }

}
