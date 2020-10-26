/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 18. October 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;


/**
 * A generic interface for all {@link HierarchicalStreamReader} implementations reading a DOM.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.1
 */
public interface DocumentReader extends HierarchicalStreamReader {

    /**
     * Retrieve the current processed node of the DOM.
     * 
     * @return the current node
     * @since 1.2.1
     */
    public Object getCurrent();
}
