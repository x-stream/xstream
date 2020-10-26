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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A {@link HierarchicalStreamDriver} for XPP DOM using the XmlPullParserFactory to locate an parser.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class XppDomDriver extends AbstractXppDomDriver {

    public XppDomDriver() {
        super(new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public XppDomDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link XppDomDriver#XppDomDriver(NameCoder)} instead.
     */
    @Deprecated
    public XppDomDriver(final XmlFriendlyReplacer replacer) {
        super(replacer);
    }

    @Override
    protected synchronized XmlPullParser createParser() throws XmlPullParserException {
        return XppDriver.createDefaultParser();
    }
}
