package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.converters.ErrorWriter;

public interface HierarchicalStreamReader {

    boolean hasMoreChildren();

    void moveDown();

    void moveUp();

    String getNodeName();

    String getValue();

    String getAttribute(String name);

    /** @deprecated */
    Object peekUnderlyingNode();

    void appendErrors(ErrorWriter errorWriter);

}
