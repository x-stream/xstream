/*
 * Copyright (C) 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.naming.NoNameCoder;


/**
 * Abstract base class for all HierarchicalStreamDriver implementations. Implementations of
 * {@link HierarchicalStreamDriver} should rather be derived from this class then implementing
 * the interface directly.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public abstract class AbstractDriver implements HierarchicalStreamDriver {

    private NameCoder replacer;

    /**
     * Creates an AbstractDriver with a NameCoder that does nothing.
     */
    public AbstractDriver() {
        this(new NoNameCoder());
    }

    /**
     * Creates an AbstractDriver with a provided {@link NameCoder}.
     * 
     * @param nameCoder the name coder for the target format
     */
    public AbstractDriver(NameCoder nameCoder) {
        this.replacer = nameCoder;
    }

    protected NameCoder getNameCoder() {
        return replacer;
    }

    /**
     * {@inheritDoc}
     */
    public HierarchicalStreamReader createReader(URL in) {
        InputStream stream = null;
        try {
            stream = in.openStream();
        } catch (IOException e) {
            throw new StreamException(e);
        }
        return createReader(stream);
    }

    /**
     * {@inheritDoc}
     */
    public HierarchicalStreamReader createReader(File in) {
        try {
            return createReader(new FileInputStream(in));
        } catch (FileNotFoundException e) {
            throw new StreamException(e);
        }
    }
}
