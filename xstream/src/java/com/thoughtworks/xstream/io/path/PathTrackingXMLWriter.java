package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PathTrackingXMLWriter implements HierarchicalStreamWriter {

    private HierarchicalStreamWriter targetWriter;
    private PathTracker pathTracker;

    public PathTrackingXMLWriter(HierarchicalStreamWriter xmlWriter, PathTracker pathTracker) {
        this.targetWriter = xmlWriter;
        this.pathTracker = pathTracker;
    }

    public void startElement(String name) {
        pathTracker.pushElement(name);
        targetWriter.startElement(name);
    }

    public void addAttribute(String key, String value) {
        targetWriter.addAttribute(key, value);
    }

    public void writeText(String text) {
        targetWriter.writeText(text);
    }

    public void endElement() {
        targetWriter.endElement();
        pathTracker.popElement();
    }

}
