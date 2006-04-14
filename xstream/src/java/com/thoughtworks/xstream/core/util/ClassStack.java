package com.thoughtworks.xstream.core.util;

// TODO: Do we need this? All we save is a single cast in the TreeUnmarshaller compared to the usage of FastStack.
public final class ClassStack {

    private Class[] stack;
    private int pointer;

    public ClassStack(int initialCapacity) {
        stack = new Class[initialCapacity];
    }

    public void push(Class value) {
        if (pointer + 1 >= stack.length) {
            resizeStack(stack.length * 2);
        }
        stack[pointer++] = value;
    }

    public void popSilently() {
        stack[--pointer] = null;
    }

    public Class peek() {
        return pointer == 0 ? null : stack[pointer - 1];
    }

    public int size() {
        return pointer;
    }

    public Class get(int i) {
        return stack[i];
    }

    private void resizeStack(int newCapacity) {
        Class[] newStack = new Class[newCapacity];
        System.arraycopy(stack, 0, newStack, 0, Math.min(stack.length, newCapacity));
        stack = newStack;
    }
}
