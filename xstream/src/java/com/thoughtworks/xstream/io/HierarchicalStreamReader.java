package com.thoughtworks.xstream.io;

public interface HierarchicalStreamReader {

    String name();

    String text();

    String attribute(String name);

    boolean nextChild();

    void pop();
    
    Object peek();
}
