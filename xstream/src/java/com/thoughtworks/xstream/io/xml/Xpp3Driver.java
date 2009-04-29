/*
 * Copyright (C) 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. April 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;


import com.thoughtworks.xstream.io.HierarchicalStreamDriver;

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;


/**
 * A {@link HierarchicalStreamDriver} using the Xpp3 parser.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class Xpp3Driver extends AbstractXppDriver {

    public Xpp3Driver() {
        super(new XmlFriendlyReplacer());
    }

    /**
     * @since upcoming
     */
    public Xpp3Driver(XmlFriendlyReplacer replacer) {
        super(replacer);
    }

    /**
     * @since upcoming
     */
    protected XmlPullParser createParser() {
        return new MXParser();
    }
}
