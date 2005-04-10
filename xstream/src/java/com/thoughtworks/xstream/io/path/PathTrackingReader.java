package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.ReaderWrapper;

/**
 * Wrapper for HierarchicalStreamReader that tracks the path (a subset of XPath) of the current node that is being read.
 *
 * @see PathTracker
 * @see Path
 *
 * @author Joe Walnes
 */
public class PathTrackingReader extends ReaderWrapper {

    private final PathTracker pathTracker;

    public PathTrackingReader(HierarchicalStreamReader reader, PathTracker pathTracker) {
        super(reader);
        this.pathTracker = pathTracker;
        pathTracker.pushElement(getNodeName());
    }

    public void moveDown() {
        super.moveDown();
        pathTracker.pushElement(getNodeName());
    }

    public void moveUp() {
        super.moveUp();
        pathTracker.popElement();
    }

    public void appendErrors(ErrorWriter errorWriter) {
        errorWriter.add("path", pathTracker.getPath().toString());
        super.appendErrors(errorWriter);
    }

}
