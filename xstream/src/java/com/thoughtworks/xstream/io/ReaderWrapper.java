/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 10. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.converters.ErrorWriter;

import java.util.Iterator;

/**
 * Base class to make it easy to create wrappers (decorators) for HierarchicalStreamReader.
 *
 * @author Joe Walnes
 */
public abstract class ReaderWrapper implements HierarchicalStreamReader {

    protected HierarchicalStreamReader wrapped;

    protected ReaderWrapper(HierarchicalStreamReader reader) {
        this.wrapped = reader;
    }

    public boolean hasMoreChildren() {
        return wrapped.hasMoreChildren();
    }

    public void moveDown() {
        wrapped.moveDown();
    }

    public void moveUp() {
        wrapped.moveUp();
    }

    public String getNodeName() {
        return wrapped.getNodeName();
    }

    public String getValue() {
        return wrapped.getValue();
    }

    public String getAttribute(String name) {
        return wrapped.getAttribute(name);
    }

    public String getAttribute(int index) {
        return wrapped.getAttribute(index);
    }

    public int getAttributeCount() {
        return wrapped.getAttributeCount();
    }

    public String getAttributeName(int index) {
        return wrapped.getAttributeName(index);
    }

    public Iterator getAttributeNames() {
        return wrapped.getAttributeNames();
    }

    public void appendErrors(ErrorWriter errorWriter) {
        wrapped.appendErrors(errorWriter);
    }

    public void close() {
        wrapped.close();
    }

    public HierarchicalStreamReader underlyingReader() {
        return wrapped.underlyingReader();
    }
}
