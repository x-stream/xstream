package com.thoughtworks.xstream.core.util;

public final class StringStack {

    private String[] stack;
    private int pointer;

    public StringStack(int initialCapacity) {
        stack = new String[initialCapacity];
    }

    public void push(String value) {
        if (pointer + 1 >= stack.length) {
            resizeStack(stack.length * 2);
        }
        stack[pointer++] = value;
    }

    public void popSilently() {
        pointer--;
    }

    public String pop() {
        return stack[--pointer];
    }

    public String peek() {
        return pointer == 0 ? null : stack[pointer - 1];
    }

    public int size() {
        return pointer;
    }

    public String get(int i) {
        return stack[i];
    }

    private void resizeStack(int newCapacity) {
        String[] newStack = new String[newCapacity];
        System.arraycopy(stack, 0, newStack, 0, Math.min(stack.length, newCapacity));
        stack = newStack;
    }

}
