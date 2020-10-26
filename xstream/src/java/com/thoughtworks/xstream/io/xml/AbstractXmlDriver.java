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

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * Base class for HierarchicalStreamDrivers to use XML-based HierarchicalStreamReader and HierarchicalStreamWriter.
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @since 1.2
 * @deprecated As of 1.4
 */
@Deprecated
public abstract class AbstractXmlDriver extends AbstractDriver {

    /**
     * Creates a AbstractXmlFriendlyDriver with default XmlFriendlyReplacer
     * 
     * @deprecated As of 1.4
     */
    @Deprecated
    public AbstractXmlDriver() {
        this(new XmlFriendlyNameCoder());
    }

    /**
     * Creates a AbstractXmlFriendlyDriver with default XmlFriendlyReplacer
     * 
     * @since 1.4
     * @deprecated As of 1.4
     */
    @Deprecated
    public AbstractXmlDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * Creates a AbstractXmlFriendlyDriver with custom XmlFriendlyReplacer
     * 
     * @param replacer the XmlFriendlyReplacer
     * @deprecated As of 1.4
     */
    @Deprecated
    public AbstractXmlDriver(final XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    /**
     * @deprecated As of 1.4
     */
    @Deprecated
    protected XmlFriendlyReplacer xmlFriendlyReplacer() {
        final NameCoder nameCoder = getNameCoder();
        return nameCoder instanceof XmlFriendlyReplacer ? (XmlFriendlyReplacer)nameCoder : null;
    }

}
