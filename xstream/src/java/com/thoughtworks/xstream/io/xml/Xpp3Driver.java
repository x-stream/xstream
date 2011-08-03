/*
 * Copyright (C) 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. April 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;


import java.io.Reader;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A {@link HierarchicalStreamDriver} using the Xpp3 parser.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class Xpp3Driver extends AbstractXppDriver {

    /**
     * Construct an Xpp3Driver.
     * 
     * @since upcoming
     */
    public Xpp3Driver() {
        super(new XmlFriendlyNameCoder());
    }

    /**
     * Construct an Xpp3Driver.
     * 
     * @param nameCoder the replacer for XML friendly names
     * @since upcoming
     */
    public Xpp3Driver(NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * {@inheritDoc}
     */
    public HierarchicalStreamReader createReader(Reader in) {
        return new Xpp3Reader(in, getNameCoder());
    }
}
