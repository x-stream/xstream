package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.WriterWrapper;

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

    public PathTrackingWriter(HierarchicalStreamWriter writer, PathTracker pathTracker) {
        super(writer);
        this.pathTracker = pathTracker;
    }

    public void startNode(String name) {
        pathTracker.pushElement(name);
        super.startNode(name); 
    }

    public void startNode(String name, Class clazz) {
        pathTracker.pushElement(name);
        super.startNode(name, clazz);
    }

    public void endNode() {
        super.endNode();
        pathTracker.popElement();
    }

}
