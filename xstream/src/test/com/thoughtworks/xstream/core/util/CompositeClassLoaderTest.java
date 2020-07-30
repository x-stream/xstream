/*
 * Copyright (C) 2020 Steve Davidson
 * Copyright (C) 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. July 2020 by Steve Davidson
 */

package com.thoughtworks.xstream.core.util;

import junit.framework.TestCase;

public class CompositeClassLoaderTest extends TestCase {

    class DummyClassLoader extends ClassLoader {
        public DummyClassLoader(){
            //end init
        }
    }

    public void testAdd(){
        //Just check that Exceptions don't get thrown
        final CompositeClassLoader cl = new CompositeClassLoader();
        ClassLoader testCl = new DummyClassLoader();
        cl.add(testCl);
        cl.add(testCl);
        testCl = null;
        cl.add(testCl);
    }
}

