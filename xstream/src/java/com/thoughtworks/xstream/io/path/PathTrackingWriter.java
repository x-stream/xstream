package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PathTrackingWriter implements HierarchicalStreamWriter {

    private HierarchicalStreamWriter targetWriter;
    private PathTracker pathTracker;

    public PathTrackingWriter(HierarchicalStreamWriter targetWriter, PathTracker pathTracker) {
        this.targetWriter = targetWriter;
        this.pathTracker = pathTracker;
    }

    public void startNode(String name) {
        pathTracker.pushElement(name);
        targetWriter.startNode(name);
    }

    public void addAttribute(String key, String value) {
        targetWriter.addAttribute(key, value);
    }

    public void setValue(String text) {
        targetWriter.setValue(text);
    }

    public void endNode() {
        targetWriter.endNode();
        pathTracker.popElement();
    }

}
