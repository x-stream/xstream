/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 08. January 2014 by Joerg Schaible, factored out from SunUnsafeReflectionProviderTest
 */
package com.thoughtworks.xstream.converters.reflection;

public class SunLimitedUnsafeReflectionProviderTest extends SunUnsafeReflectionProviderTest {

    // inherits tests from superclass

    public ReflectionProvider createReflectionProvider() {
        return new SunLimitedUnsafeReflectionProvider();
    }
}
