/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 09. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Permission for <code>null</code> or XStream's null replacement type.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class NullPermission implements TypePermission {
    /**
     * @since upcoming
     */
    public static final TypePermission NULL = new NullPermission();

    public boolean allows(Class type) {
        return type == null || type == Mapper.Null.class;
    }
}
