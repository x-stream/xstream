package com.thoughtworks.xstream.xml;

//@TODO: Alter API to be pull-parser friendly.

public interface XMLReader {

    String name();

    String text();

    String attribute(String name);

    int childCount();

    boolean childExists(String elementName);

    void child(int index);

    void child(String elementName);

    void pop();

}
