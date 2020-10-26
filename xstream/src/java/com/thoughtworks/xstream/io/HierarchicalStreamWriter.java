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

/**
 * @author Joe Walnes
 */
public interface HierarchicalStreamWriter extends AutoCloseable {

    void startNode(String name);

    /**
     * @since upcoming, was originally added to ExtendedHierarchicalStreamWriter.
     */
    void startNode(String name, Class<?> clazz);

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
    @Override
    void close();

    /**
     * Return the underlying HierarchicalStreamWriter implementation.
     * <p>
     * If a Converter needs to access methods of a specific HierarchicalStreamWriter implementation that are not defined
     * in the HierarchicalStreamWriter interface, it should call this method before casting. This is because the writer
     * passed to the Converter is often wrapped/decorated by another implementation to provide additional functionality
     * (such as XPath tracking).
     * </p>
     * <p>
     * For example:
     * </p>
     *
     * <pre>
     * MySpecificWriter mySpecificWriter = (MySpecificWriter)writer; <b>// INCORRECT!</b>
     * mySpecificWriter.doSomethingSpecific();
     * </pre>
     *
     * <pre>
     * MySpecificWriter mySpecificWriter = (MySpecificWriter)writer.underlyingWriter();  <b>// CORRECT!</b>
     * mySpecificWriter.doSomethingSpecific();
     * </pre>
     * <p>
     * Implementations of HierarchicalStreamWriter should return 'this', unless they are a decorator, in which case they
     * should delegate to whatever they are wrapping.
     * </p>
     */
    HierarchicalStreamWriter underlyingWriter();

}
