package com.thoughtworks.xstream.xml;

public interface XMLReader {

    String name();

    String text();

    String attribute(String name);

    boolean nextChild();

    void pop();
    
    Object peek();
}
