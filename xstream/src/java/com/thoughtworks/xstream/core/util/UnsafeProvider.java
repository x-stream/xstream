/*
 * Copyright (C) 2026 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 3rd February 2026 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.reflection.Unsafe;


/**
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class UnsafeProvider {
    public final Unsafe unsafe;
    public final Exception exception;
    private static UnsafeProvider provider;
    static {
        Unsafe u = null;
        Exception ex;
        try {
            u = SunMiscUnsafe.theInstance();
            ex = u.getInitException();
        } catch (final Throwable e) {
            ex = new InstantiationException("sun.misc.Unsafe not available");
            ex.initCause(e);
        }
        provider = new UnsafeProvider(u, ex);
    }

    private UnsafeProvider(final Unsafe u, final Exception ex) {
        unsafe = u;
        exception = ex;
    }

    public static UnsafeProvider get() {
        return provider;
    }
}
