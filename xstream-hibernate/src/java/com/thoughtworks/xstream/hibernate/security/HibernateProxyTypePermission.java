/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 19. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.hibernate.security;

import org.hibernate.proxy.HibernateProxy;

import com.thoughtworks.xstream.security.TypePermission;


/**
 * Permission for any array type.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class HibernateProxyTypePermission implements TypePermission {
    /**
     * @since upcoming
     */
    public static final TypePermission PROXIES = new HibernateProxyTypePermission();

    public boolean allows(final Class type) {
        return type != null && HibernateProxy.class.isAssignableFrom(type);
    }

    public int hashCode() {
        return 31;
    }

    public boolean equals(final Object obj) {
        return obj != null && obj.getClass() == HibernateProxyTypePermission.class;
    }
}
