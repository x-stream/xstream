package com.thoughtworks.xstream.io;

public interface HierarchicalStreamReader {

    boolean getNextChildNode();

    String getNodeName();

    String getValue();

    String getAttribute(String name);

    void getParentNode();
    
    Object peekUnderlyingNode();
}
