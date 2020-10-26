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

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A {@link HierarchicalStreamDriver} using the kXML2 parser.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class KXml2Driver extends AbstractXppDriver {

    /**
     * Construct a KXml2Driver.
     * 
     * @since 1.4
     */
    public KXml2Driver() {
        super(new XmlFriendlyNameCoder());
    }

    /**
     * Construct a KXml2Driver.
     * 
     * @param nameCoder the replacer for XML friendly names
     * @since 1.4
     */
    public KXml2Driver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected XmlPullParser createParser() {
        return new KXmlParser();
    }
}
