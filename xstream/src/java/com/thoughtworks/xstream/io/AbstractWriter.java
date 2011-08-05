/*
 * Copyright (C) 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 17. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.core.util.Cloneables;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.naming.NoNameCoder;


/**
 * Abstract base class for all HierarchicalStreamWriter implementations. Implementations of
 * {@link HierarchicalStreamWriter} should rather be derived from this class then implementing
 * the interface directly.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public abstract class AbstractWriter implements ExtendedHierarchicalStreamWriter {

    private NameCoder nameCoder;

    /**
     * Creates an AbstractWriter with a NameCoder that does nothing.
     * 
     * @since 1.4
     */
    protected AbstractWriter() {
        this(new NoNameCoder());
    }

    /**
     * Creates an AbstractWriter with a provided {@link NameCoder}.
     * 
     * @param nameCoder the name coder used to write names in the target format
     * @since 1.4
     */
    protected AbstractWriter(NameCoder nameCoder) {
        this.nameCoder = (NameCoder)Cloneables.cloneIfPossible(nameCoder);
    }

    /**
     * {@inheritDoc}
     */
    public void startNode(String name, Class clazz) {
        startNode(name);
    }

    /**
     * {@inheritDoc}
     */
    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }

    /**
     * Encode the node name into the name of the target format.
     * 
     * @param name the original name
     * @return the name in the target format
     * @since 1.4
     */
    public String encodeNode(String name) {
        return nameCoder.encodeNode(name);
    }

    /**
     * Encode the attribute name into the name of the target format.
     * 
     * @param name the original name
     * @return the name in the target format
     * @since 1.4
     */
    public String encodeAttribute(String name) {
        return nameCoder.encodeAttribute(name);
    }
}
