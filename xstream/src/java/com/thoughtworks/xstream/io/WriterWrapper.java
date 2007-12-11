/*
 * Copyright (C) 2005, 2006 Joe Walnes.
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

/**
 * Base class to make it easy to create wrappers (decorators) for HierarchicalStreamWriter.
 *
 * @author Joe Walnes
 */
public abstract class WriterWrapper implements ExtendedHierarchicalStreamWriter {

    protected HierarchicalStreamWriter wrapped;

    protected WriterWrapper(HierarchicalStreamWriter wrapped) {
        this.wrapped = wrapped;
    }

    public void startNode(String name) {
        wrapped.startNode(name);
    }

    public void startNode(String name, Class clazz) {

        ((ExtendedHierarchicalStreamWriter) wrapped).startNode(name, clazz);
    }

    public void endNode() {
        wrapped.endNode();
    }

    public void addAttribute(String key, String value) {
        wrapped.addAttribute(key, value);
    }

    public void setValue(String text) {
        wrapped.setValue(text);
    }

    public void flush() {
        wrapped.flush();
    }

    public void close() {
        wrapped.close();
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return wrapped.underlyingWriter();
    }

}
