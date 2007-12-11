/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import junit.framework.TestCase;

public class FastStackTest extends TestCase {

    public void test() {
        FastStack stack = new FastStack(2);

        stack.push("a");
        stack.push("b");
        stack.push("c");
        stack.push("d");

        assertEquals("d", stack.peek());
        assertEquals("d", stack.peek());
        assertEquals("d", stack.pop());
        assertEquals("c", stack.pop());
        stack.popSilently();
        assertEquals("a", stack.peek());
        assertEquals("a", stack.pop());
    }
}
