package com.thoughtworks.xstream.io;

public interface HierarchicalStreamReader {

    boolean hasMoreChildren();
    void moveDown();
    void moveUp();

    String getNodeName();
    String getValue();
    String getAttribute(String name);

    Object peekUnderlyingNode();

}
