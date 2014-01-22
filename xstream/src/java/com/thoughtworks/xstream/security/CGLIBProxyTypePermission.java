/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 19. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

import net.sf.cglib.proxy.Proxy;


/**
 * Permission for any array type.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class CGLIBProxyTypePermission implements TypePermission {
    /**
     * @since upcoming
     */
    public static final TypePermission PROXIES = new CGLIBProxyTypePermission();

    public boolean allows(final Class type) {
        return type != null && Proxy.isProxyClass(type);
    }

    public int hashCode() {
        return 19;
    }

    public boolean equals(final Object obj) {
        return obj != null && obj.getClass() == CGLIBProxyTypePermission.class;
    }
}
