package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class PathTrackingReader implements HierarchicalStreamReader {
    private HierarchicalStreamReader reader;
    private PathTracker pathTracker;

    public PathTrackingReader(HierarchicalStreamReader reader, PathTracker pathTracker) {
        this.reader = reader;
        this.pathTracker = pathTracker;
        pathTracker.pushElement(getNodeName());
    }

    public boolean hasMoreChildren() {
        return reader.hasMoreChildren();
    }

    public void moveDown() {
        reader.moveDown();
        pathTracker.pushElement(getNodeName());
    }

    public void moveUp() {
        reader.moveUp();
        pathTracker.popElement();
    }

    public String getNodeName() {
        return reader.getNodeName();
    }

    public String getValue() {
        return reader.getValue();
    }

    public String getAttribute(String name) {
        return reader.getAttribute(name);
    }

    public Object peekUnderlyingNode() {
        return reader.peekUnderlyingNode();
    }

    public void appendErrors(ErrorWriter errorWriter) {
        errorWriter.add("path", pathTracker.getCurrentPath());
        reader.appendErrors(errorWriter);
    }

    public void close() {
        reader.close();
    }

    public HierarchicalStreamReader underlyingReader() {
        return reader.underlyingReader();
    }
}
