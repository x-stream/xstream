package com.thoughtworks.xstream.xml;

//@TODO: Alter API to be pull-parser friendly.

public interface XMLReader {

    String name();

    String text();

    String attribute(String name);

    int childCount();

    void child(int index);

    void pop();

}
