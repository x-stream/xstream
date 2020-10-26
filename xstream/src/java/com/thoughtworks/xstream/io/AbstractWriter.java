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

import com.thoughtworks.xstream.core.util.Cloneables;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.naming.NoNameCoder;


/**
 * Abstract base class for all HierarchicalStreamWriter implementations. Implementations of
 * {@link HierarchicalStreamWriter} should rather be derived from this class then implementing the interface directly.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public abstract class AbstractWriter implements ExtendedHierarchicalStreamWriter {

    private final NameCoder nameCoder;

    /**
     * Creates an AbstractWriter with a NameCoder that does nothing.
     * 
     * @since 1.4
     */
    protected AbstractWriter() {
        this(new NoNameCoder());
    }

    /**
     * Creates an AbstractWriter with a provided {@link NameCoder}.
     * 
     * @param nameCoder the name coder used to write names in the target format
     * @since 1.4
     */
    protected AbstractWriter(final NameCoder nameCoder) {
        this.nameCoder = Cloneables.cloneIfPossible(nameCoder);
    }

    @Override
    public void startNode(final String name, final Class<?> clazz) {
        startNode(name);
    }

    @Override
    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }

    /**
     * Encode the node name into the name of the target format.
     * 
     * @param name the original name
     * @return the name in the target format
     * @since 1.4
     */
    public String encodeNode(final String name) {
        return nameCoder.encodeNode(name);
    }

    /**
     * Encode the attribute name into the name of the target format.
     * 
     * @param name the original name
     * @return the name in the target format
     * @since 1.4
     */
    public String encodeAttribute(final String name) {
        return nameCoder.encodeAttribute(name);
    }
}
