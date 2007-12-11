/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io;

/**
 * @author Joe Walnes
 */
public interface HierarchicalStreamWriter {

    void startNode(String name);

    void addAttribute(String name, String value);

    /**
     * Write the value (text content) of the current node.
     */
    void setValue(String text);

    void endNode();

    /**
     * Flush the writer, if necessary.
     */
    void flush();

    /**
     * Close the writer, if necessary.
     */
    void close();

    /**
     * Return the underlying HierarchicalStreamWriter implementation.
     *
     * <p>If a Converter needs to access methods of a specific HierarchicalStreamWriter implementation that are not
     * defined in the HierarchicalStreamWriter interface, it should call this method before casting. This is because
     * the writer passed to the Converter is often wrapped/decorated by another implementation to provide additional
     * functionality (such as XPath tracking).</p>
     *
     * <p>For example:</p>
     * <pre>MySpecificWriter mySpecificWriter = (MySpecificWriter)writer; <b>// INCORRECT!</b>
     * mySpecificWriter.doSomethingSpecific();</pre>

     * <pre>MySpecificWriter mySpecificWriter = (MySpecificWriter)writer.underlyingWriter();  <b>// CORRECT!</b>
     * mySpecificWriter.doSomethingSpecific();</pre>
     *
     * <p>Implementations of HierarchicalStreamWriter should return 'this', unless they are a decorator, in which case
     * they should delegate to whatever they are wrapping.</p>
     */
    HierarchicalStreamWriter underlyingWriter();

}
