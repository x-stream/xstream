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

import com.thoughtworks.xstream.io.AbstractReader;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * Abstract base implementation of HierarchicalStreamReader that provides common functionality to all XML-based readers.
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @since 1.2
 * @deprecated As of 1.4, use {@link AbstractReader} instead.
 */
@Deprecated
public abstract class AbstractXmlReader extends AbstractReader /* implements XmlFriendlyReader */{

    protected AbstractXmlReader() {
        this(new XmlFriendlyNameCoder());
    }

    /**
     * @deprecated As of 1.4, use {@link AbstractReader} instead.
     */
    @Deprecated
    protected AbstractXmlReader(final XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    protected AbstractXmlReader(final NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * Unescapes XML-friendly name (node or attribute)
     * 
     * @param name the escaped XML-friendly name
     * @return An unescaped name with original characters
     * @deprecated As of 1.4, use {@link #decodeNode(String)} or {@link #decodeAttribute(String)} instead.
     */
    @Deprecated
    public String unescapeXmlName(final String name) {
        return decodeNode(name);
    }

    /**
     * Escapes XML-unfriendly name (node or attribute)
     * 
     * @param name the unescaped XML-unfriendly name
     * @return An escaped name with original characters
     * @deprecated As of 1.4, use {@link AbstractReader} instead.
     */
    @Deprecated
    protected String escapeXmlName(final String name) {
        return encodeNode(name);
    }

}
