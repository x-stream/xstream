package com.thoughtworks.xstream.xml;

public interface XMLWriter {

    void attribute(String key, String value);

    void text(String text);

    void pushElement(String name);

    void pop();

}
