/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2012, 2013 XStream Committers.
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

    public void testDoesIgnoreLinkageErrors() {
        try {
            assertNull(JVM.loadClassForName("com.thoughtworks.xstream.core.EvilClass"));
        } catch (LinkageError error) {
            fail("Error thrown");
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
