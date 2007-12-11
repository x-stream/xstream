/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 23. July 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import junit.framework.TestCase;

public class JVMTest extends TestCase {

    public void testDoesNotInsantiateStaticBlocksWhenLoadingClasses() {
        try {
            new JVM().loadClass("com.thoughtworks.xstream.core.EvilClass");
        } catch (ExceptionInInitializerError error) {
            fail("Static block was called");
        }
    }
}

class EvilClass {

    static {
        evil();
    }

    static void evil() {
        throw new RuntimeException("Evil");
    }
}
