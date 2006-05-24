package com.thoughtworks.xstream.core.util;

public final class FastStack {

    private Object[] stack;
    private int pointer;

    public FastStack(int initialCapacity) {
        stack = new Object[initialCapacity];
    }

    public Object push(Object value) {
        if (pointer + 1 >= stack.length) {
            resizeStack(stack.length * 2);
        }
        stack[pointer++] = value;
        return value;
    }

    public void popSilently() {
        stack[--pointer] = null;
    }

    public Object pop() {
        final Object result = stack[--pointer]; 
        stack[pointer] = null; 
        return result;
    }

    public Object peek() {
        return pointer == 0 ? null : stack[pointer - 1];
    }

    public int size() {
        return pointer;
    }

    public boolean hasStuff() {
        return pointer > 0;
    }

    public Object get(int i) {
        return stack[i];
    }

    private void resizeStack(int newCapacity) {
        Object[] newStack = new Object[newCapacity];
        System.arraycopy(stack, 0, newStack, 0, Math.min(pointer, newCapacity));
        stack = newStack;
    }

    public String toString() {
        StringBuffer result = new StringBuffer("[");
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
