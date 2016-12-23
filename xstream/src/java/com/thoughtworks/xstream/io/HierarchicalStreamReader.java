/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.converters.ErrorReporter;
import com.thoughtworks.xstream.converters.ErrorWriter;

import java.util.Iterator;

/**
 * @author Joe Walnes
 */
public interface HierarchicalStreamReader extends ErrorReporter {

    /**
     * Does the node have any more children remaining that have not yet been read?
     */
    boolean hasMoreChildren();

    /**
     * Select the current child as current node.
     * A call to this function must be balanced with a call to {@link #moveUp()}.
     */
    void moveDown();

    /**
     * Select the parent node as current node.
     */
    void moveUp();

    /**
     * Get the name of the current node.
     */
    String getNodeName();

    /**
     * Get the value (text content) of the current node.
     */
    String getValue();

    /**
     * Get the value of an attribute of the current node.
     * <p>
     * If no such attribute exists, the method returns null.
     * </p>
     */
    String getAttribute(String name);

    /**
     * Get the value of an attribute of the current node, by index.
     * <p>
     * Note, the behavior of this method is dependent on the underlying parser when calling it with a non-existing
     * index. Typically some kind of RuntimeException is thrown.
     * </p>
     */
    String getAttribute(int index);
    
    /**
     * Number of attributes in current node.
     */
    int getAttributeCount();

    /**
     * Name of attribute in current node.
     * <p>
     * Note, the behavior of this method is dependent on the underlying parser when calling it with a non-existing
     * index. Typically some kind of RuntimeException is thrown.
     * </p>
     */
    String getAttributeName(int index);

    /**
     * Iterator with the names of the attributes.
     * <p>
     * Note, the iterator is only valid as long as the internal state of the underlying parser is still at the start of
     * the current element. The behavior is undefined if the parser moved on.
     * </p>
     */
    Iterator getAttributeNames();

    /**
     * If any errors are detected, allow the reader to add any additional information that can aid debugging
     * (such as line numbers, XPath expressions, etc).
     */
    void appendErrors(ErrorWriter errorWriter);

    /**
     * Close the reader, if necessary.
     */
    void close();

    /**
     * Return the underlying HierarchicalStreamReader implementation.
     *
     * <p>If a Converter needs to access methods of a specific HierarchicalStreamReader implementation that are not
     * defined in the HierarchicalStreamReader interface, it should call this method before casting. This is because
     * the reader passed to the Converter is often wrapped/decorated by another implementation to provide additional
     * functionality (such as XPath tracking).</p>
     *
     * <p>For example:</p>
     * <pre>MySpecificReader mySpecificReader = (MySpecificReader)reader; <b>// INCORRECT!</b>
     * mySpecificReader.doSomethingSpecific();</pre>

     * <pre>MySpecificReader mySpecificReader = (MySpecificReader)reader.underlyingReader();  <b>// CORRECT!</b>
     * mySpecificReader.doSomethingSpecific();</pre>
     *
     * <p>Implementations of HierarchicalStreamReader should return 'this', unless they are a decorator, in which case
     * they should delegate to whatever they are wrapping.</p>
     */
    HierarchicalStreamReader underlyingReader();

}
