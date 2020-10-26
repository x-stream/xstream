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

/**
 * A XmlFriendlyNameCoder to support backward compatibility with XStream 1.1.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class XStream11NameCoder extends XmlFriendlyNameCoder {

    /**
     * {@inheritDoc} Noop implementation that does not decode. Used for XStream 1.1 compatibility.
     */
    @Override
    public String decodeAttribute(final String attributeName) {
        return attributeName;
    }

    /**
     * {@inheritDoc} Noop implementation that does not decode. Used for XStream 1.1 compatibility.
     */
    @Override
    public String decodeNode(final String elementName) {
        return elementName;
    }
}
