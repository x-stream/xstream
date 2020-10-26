/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
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
