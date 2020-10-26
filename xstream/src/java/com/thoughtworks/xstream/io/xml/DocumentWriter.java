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

package com.thoughtworks.xstream.io.xml;

import java.util.List;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * A generic interface for all {@link HierarchicalStreamWriter} implementations generating a DOM.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.1
 */
public interface DocumentWriter<E> extends HierarchicalStreamWriter {

    /**
     * Retrieve a {@link List} with the top elements.
     * <p>
     * In the standard use case this list will only contain a single element. Additional elements can only occur, if
     * {@link HierarchicalStreamWriter#startNode(String)} of the implementing {@link HierarchicalStreamWriter} was
     * called multiple times with an empty node stack. Such a situation occurs calling
     * {@link com.thoughtworks.xstream.XStream#marshal(Object, HierarchicalStreamWriter)} multiple times directly.
     * </p>
     * 
     * @return a {@link List} with top nodes
     * @since 1.2.1
     */
    List<E> getTopLevelNodes();
}
