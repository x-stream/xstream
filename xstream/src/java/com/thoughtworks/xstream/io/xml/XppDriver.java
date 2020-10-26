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
import org.xmlpull.v1.XmlPullParserFactory;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A {@link HierarchicalStreamDriver} using the XmlPullParserFactory to locate an XML Pull Parser.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class XppDriver extends AbstractXppDriver {

    private static XmlPullParserFactory factory;

    public XppDriver() {
        super(new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public XppDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link XppDriver#XppDriver(NameCoder)} instead.
     */
    @Deprecated
    public XppDriver(final XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    /**
     * Create a default XML Pull Parser. The method uses the Java Service API to get the registered
     * {@link XmlPullParserFactory} and let it create a new parser.
     *
     * @return a new instance of an XML Pull Parser
     * @throws XmlPullParserException if the creation of a new parser fails.
     * @since 1.4.11
     */
    public static synchronized XmlPullParser createDefaultParser() throws XmlPullParserException {
        if (factory == null) {
            factory = XmlPullParserFactory.newInstance();
        }
        return factory.newPullParser();
    }

    @Override
    protected XmlPullParser createParser() throws XmlPullParserException {
        return createDefaultParser();
    }
}
