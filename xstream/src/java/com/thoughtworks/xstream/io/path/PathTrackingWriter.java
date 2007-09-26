package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.WriterWrapper;
import com.thoughtworks.xstream.io.xml.XmlFriendlyWriter;

/**
 * Wrapper for HierarchicalStreamWriter that tracks the path (a subset of XPath) of the current node that is being written.
 *
 * @see PathTracker
 * @see Path
 *
 * @author Joe Walnes
 */
public class PathTrackingWriter extends WriterWrapper {

    private final PathTracker pathTracker;
    private final boolean isXmlFriendly;

    public PathTrackingWriter(HierarchicalStreamWriter writer, PathTracker pathTracker) {
        super(writer);
        this.isXmlFriendly = writer.underlyingWriter() instanceof XmlFriendlyWriter;
        this.pathTracker = pathTracker;
    }

    public void startNode(String name) {
        pathTracker.pushElement(isXmlFriendly ? ((XmlFriendlyWriter)wrapped.underlyingWriter()).escapeXmlName(name) : name);
        super.startNode(name); 
    }

    public void startNode(String name, Class clazz) {
        pathTracker.pushElement(isXmlFriendly ? ((XmlFriendlyWriter)wrapped.underlyingWriter()).escapeXmlName(name) : name);
        super.startNode(name, clazz);
    }

    public void endNode() {
        super.endNode();
        pathTracker.popElement();
    }

}
