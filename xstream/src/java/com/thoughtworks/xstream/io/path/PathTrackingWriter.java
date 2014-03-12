/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.io.AbstractWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.WriterWrapper;


/**
 * Wrapper for HierarchicalStreamWriter that tracks the path (a subset of XPath) of the current node that is being
 * written.
 * 
 * @see PathTracker
 * @see Path
 * @author Joe Walnes
 */
public class PathTrackingWriter extends WriterWrapper {

    private final PathTracker pathTracker;
    private final boolean isNameEncoding;

    public PathTrackingWriter(final HierarchicalStreamWriter writer, final PathTracker pathTracker) {
        super(writer);
        isNameEncoding = writer.underlyingWriter() instanceof AbstractWriter;
        this.pathTracker = pathTracker;
    }

    @Override
    public void startNode(final String name) {
        pathTracker.pushElement(isNameEncoding ? ((AbstractWriter)wrapped.underlyingWriter()).encodeNode(name) : name);
        super.startNode(name);
    }

    @Override
    public void startNode(final String name, final Class<?> clazz) {
        pathTracker.pushElement(isNameEncoding ? ((AbstractWriter)wrapped.underlyingWriter()).encodeNode(name) : name);
        super.startNode(name, clazz);
    }

    @Override
    public void endNode() {
        super.endNode();
        pathTracker.popElement();
    }
}
