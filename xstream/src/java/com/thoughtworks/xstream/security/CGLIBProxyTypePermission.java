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
 * @since 1.4.7
 */
public class CGLIBProxyTypePermission implements TypePermission {
    /**
     * @since 1.4.7
     */
    public static final TypePermission PROXIES = new CGLIBProxyTypePermission();

    @Override
    public boolean allows(final Class<?> type) {
        return type != null && type != Object.class && !type.isInterface()
            && (Proxy.isProxyClass(type) || type.getName().startsWith(Proxy.class.getPackage().getName() + "."));
    }

    @Override
    public int hashCode() {
        return 19;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj.getClass() == CGLIBProxyTypePermission.class;
    }
}
