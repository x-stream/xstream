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

import java.util.List;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * A generic interface for all {@link HierarchicalStreamWriter} implementations generating a
 * DOM.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.1
 */
public interface DocumentWriter extends HierarchicalStreamWriter {

    /**
     * Retrieve a {@link List} with the top elements. In the standard use case this list will
     * only contain a single element. Additional elements can only occur, if
     * {@link HierarchicalStreamWriter#startNode(String)} of the implementing
     * {@link HierarchicalStreamWriter} was called multiple times with an empty node stack. Such
     * a situation occurs calling
     * {@link com.thoughtworks.xstream.XStream#marshal(Object, HierarchicalStreamWriter)}
     * multiple times directly.
     * 
     * @return a {@link List} with top nodes
     * @since 1.2.1
     */
    List getTopLevelNodes();
}
