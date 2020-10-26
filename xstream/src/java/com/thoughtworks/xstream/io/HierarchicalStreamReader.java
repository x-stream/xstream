/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.io;

import java.util.Iterator;

import com.thoughtworks.xstream.converters.ErrorReporter;
import com.thoughtworks.xstream.converters.ErrorWriter;


/**
 * @author Joe Walnes
 */
public interface HierarchicalStreamReader extends ErrorReporter, AutoCloseable {

    /**
     * Does the node have any more children remaining that have not yet been read?
     */
    boolean hasMoreChildren();

    /**
     * Peek the name of the next child. In situation where {@link #hasMoreChildren()} returns true, peek the tag name of
     * the child.
     *
     * @since upcoming, was originally added in 1.4.2 to ExtendedHierarchicalStreamReader.
     */
    String peekNextChild();

    /**
     * Select the current child as current node. A call to this function must be balanced with a call to
     * {@link #moveUp()}.
     */
    void moveDown();

    /**
     * Select the parent node as current node.
     */
    void moveUp();

    /**
     * Retrieve the current nesting level. The method counts the number of unbalanced calls to {@link #moveDown()} and
     * {@link #moveUp()}.
     *
     * @return the current nesting level
     * @since upcoming
     */
    int getLevel();

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
    Iterator<String> getAttributeNames();

    /**
     * If any errors are detected, allow the reader to add any additional information that can aid debugging (such as
     * line numbers, XPath expressions, etc).
     */
    @Override
    void appendErrors(ErrorWriter errorWriter);

    /**
     * Close the reader, if necessary.
     */
    @Override
    void close();

    /**
     * Return the underlying HierarchicalStreamReader implementation.
     * <p>
     * If a Converter needs to access methods of a specific HierarchicalStreamReader implementation that are not defined
     * in the HierarchicalStreamReader interface, it should call this method before casting. This is because the reader
     * passed to the Converter is often wrapped/decorated by another implementation to provide additional functionality
     * (such as XPath tracking).
     * </p>
     * <p>
     * For example:
     * </p>
     *
     * <pre>
     * MySpecificReader mySpecificReader = (MySpecificReader)reader; <b>// INCORRECT!</b>
     * mySpecificReader.doSomethingSpecific();
     * </pre>
     *
     * <pre>
     * MySpecificReader mySpecificReader = (MySpecificReader)reader.underlyingReader();  <b>// CORRECT!</b>
     * mySpecificReader.doSomethingSpecific();
     * </pre>
     * <p>
     * Implementations of HierarchicalStreamReader should return 'this', unless they are a decorator, in which case they
     * should delegate to whatever they are wrapping.
     * </p>
     */
    HierarchicalStreamReader underlyingReader();

}
