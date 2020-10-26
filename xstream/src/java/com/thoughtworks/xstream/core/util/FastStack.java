/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import java.util.Arrays;


/**
 * An array-based stack implementation.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public final class FastStack<T> {

    private T[] stack;
    private int pointer;

    public FastStack(final int initialCapacity) {
        final T[] array = getArray(initialCapacity);
        stack = array;
    }

    @SafeVarargs
    private final T[] getArray(final int capacity, final T... t) {
        return Arrays.copyOf(t, capacity);
    }

    public T push(final T value) {
        if (pointer + 1 >= stack.length) {
            resizeStack(stack.length * 2);
        }
        stack[pointer++] = value;
        return value;
    }

    public void popSilently() {
        stack[--pointer] = null;
    }

    public T pop() {
        final T result = stack[--pointer];
        stack[pointer] = null;
        return result;
    }

    public T peek() {
        return pointer == 0 ? null : stack[pointer - 1];
    }

    public Object replace(final T value) {
        final T result = stack[pointer - 1];
        stack[pointer - 1] = value;
        return result;
    }

    public void replaceSilently(final T value) {
        stack[pointer - 1] = value;
    }

    public int size() {
        return pointer;
    }

    public boolean hasStuff() {
        return pointer > 0;
    }

    public T get(final int i) {
        return stack[i];
    }

    private void resizeStack(final int newCapacity) {
        final T[] newStack = getArray(newCapacity);
        System.arraycopy(stack, 0, newStack, 0, Math.min(pointer, newCapacity));
        stack = newStack;
    }

    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer("[");
        for (int i = 0; i < pointer; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(stack[i]);
        }
        result.append(']');
        return result.toString();
    }
}
