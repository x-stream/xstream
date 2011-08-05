/*
 * Copyright (C) 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 16. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.core.util.Cloneables;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.naming.NoNameCoder;


/**
 * Abstract base class for all HierarchicalStreamReader implementations. Implementations of
 * {@link HierarchicalStreamReader} should rather be derived from this class then implementing
 * the interface directly.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public abstract class AbstractReader implements HierarchicalStreamReader {

    private NameCoder nameCoder;

    /**
     * Creates an AbstractReader with a NameCoder that does nothing.
     * 
     * @since 1.4
     */
    protected AbstractReader() {
        this(new NoNameCoder());
    }

    /**
     * Creates an AbstractReader with a provided {@link NameCoder}.
     * 
     * @param nameCoder the name coder used to read names from the incoming format
     * @since 1.4
     */
    protected AbstractReader(NameCoder nameCoder) {
        this.nameCoder = (NameCoder)Cloneables.cloneIfPossible(nameCoder);
    }

    /**
     * {@inheritDoc}
     */
    public HierarchicalStreamReader underlyingReader() {
        return this;
    }

    /**
     * Decode a node name from the target format.
     * 
     * @param name the name in the target format
     * @return the original name
     * @since 1.4
     */
    public String decodeNode(String name) {
        return nameCoder.decodeNode(name);
    }

    /**
     * Decode an attribute name from the target format.
     * 
     * @param name the name in the target format
     * @return the original name
     * @since 1.4
     */
    public String decodeAttribute(String name) {
        return nameCoder.decodeAttribute(name);
    }

    /**
     * Encode the node name again into the name of the target format. Internally used.
     * 
     * @param name the original name
     * @return the name in the target format
     * @since 1.4
     */
    protected String encodeNode(String name) {
        return nameCoder.encodeNode(name);
    }

    /**
     * Encode the attribute name again into the name of the target format. Internally used.
     * 
     * @param name the original name
     * @return the name in the target format
     * @since 1.4
     */
    protected String encodeAttribute(String name) {
        return nameCoder.encodeAttribute(name);
    }
}
