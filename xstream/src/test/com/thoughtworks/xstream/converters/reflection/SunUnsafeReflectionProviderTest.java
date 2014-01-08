/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. January 2014 by Joerg Schaible, renamed from Sun14RelfectrionProviderTest.
 */
package com.thoughtworks.xstream.converters.reflection;

public class SunUnsafeReflectionProviderTest extends SunLimitedUnsafeReflectionProviderTest {

    // inherits tests from superclass

    public ReflectionProvider createReflectionProvider() {
        return new SunUnsafeReflectionProvider();
    }
}