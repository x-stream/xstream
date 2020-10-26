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

import com.thoughtworks.xstream.io.AbstractWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * Abstract base implementation of HierarchicalStreamWriter that provides common functionality to all XML-based writers.
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @since 1.2
 * @deprecated As of 1.4 use {@link AbstractWriter} instead
 */
@Deprecated
public abstract class AbstractXmlWriter extends AbstractWriter implements XmlFriendlyWriter {

    protected AbstractXmlWriter() {
        this(new XmlFriendlyNameCoder());
    }

    /**
     * @deprecated As of 1.4
     */
    @Deprecated
    protected AbstractXmlWriter(final XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    protected AbstractXmlWriter(final NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * Escapes XML name (node or attribute) to be XML-friendly
     * 
     * @param name the unescaped XML name
     * @return An escaped name with original characters replaced
     * @deprecated As of 1.4 use {@link #encodeNode(String)} or {@link #encodeAttribute(String)} instead
     */
    @Deprecated
    @Override
    public String escapeXmlName(final String name) {
        return super.encodeNode(name);
    }

}
