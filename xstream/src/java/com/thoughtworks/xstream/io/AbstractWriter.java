/*
 * Copyright (C) 2009, 2011, 2014 XStream Committers.
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
 * {@link HierarchicalStreamWriter} should rather be derived from this class then implementing the interface directly.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public abstract class AbstractWriter implements ExtendedHierarchicalStreamWriter {

    private final NameCoder nameCoder;

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
    protected AbstractWriter(final NameCoder nameCoder) {
        this.nameCoder = Cloneables.cloneIfPossible(nameCoder);
    }

    @Override
    public void startNode(final String name, final Class<?> clazz) {
        startNode(name);
    }

    @Override
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
    public String encodeNode(final String name) {
        return nameCoder.encodeNode(name);
    }

    /**
     * Encode the attribute name into the name of the target format.
     * 
     * @param name the original name
     * @return the name in the target format
     * @since 1.4
     */
    public String encodeAttribute(final String name) {
        return nameCoder.encodeAttribute(name);
    }
}
