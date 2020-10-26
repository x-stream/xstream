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

package com.thoughtworks.xstream.io.xml.xppdom;

import java.io.Reader;

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;


/**
 * @author Jason van Zyl
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @deprecated As of 1.4, use {@link XppDom#build(XmlPullParser)} instead
 */
@Deprecated
public class Xpp3DomBuilder {
    /**
     * @deprecated As of 1.4, use {@link XppDom#build(XmlPullParser)} instead
     */
    @Deprecated
    public static Xpp3Dom build(final Reader reader) throws Exception {
        final XmlPullParser parser = new MXParser();
        parser.setInput(reader);
        try {
            return (Xpp3Dom)XppDom.build(parser);
        } finally {
            reader.close();
        }
    }
}
