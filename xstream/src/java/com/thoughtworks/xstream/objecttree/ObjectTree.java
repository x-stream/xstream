package com.thoughtworks.xstream.objecttree;

public interface ObjectTree {

    void push(String fieldName);

    void pop();

    Object get();

    Class type();

    void set(Object value);

    void create(Class type);

    String[] fieldNames();

    ObjectTree newStack(Object instance);

    ObjectTree newStack(Class type);
}
