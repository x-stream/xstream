package com.thoughtworks.xstream.xml.path;

import com.thoughtworks.xstream.xml.XMLWriter;

import java.util.Map;
import java.util.HashMap;

public class PathTrackingXMLWriter implements XMLWriter {

    private XMLWriter targetWriter;
    private PathTracker pathTracker;

    public PathTrackingXMLWriter(XMLWriter xmlWriter, PathTracker pathTracker) {
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
