/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
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
