package com.thoughtworks.xstream.io;

public interface HierarchicalStreamWriter {

    void startNode(String name);

    void addAttribute(String name, String value);

    void setValue(String text);

    void endNode();

}
