package com.thoughtworks.xstream.core.util;

import junit.framework.TestCase;

public class StringStackTest extends TestCase {

    public void test() {
        StringStack stack = new StringStack(2);

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
