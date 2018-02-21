/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml.xppdom;

/**
 * Simple Document Object Model for XmlPullParser implementations.
 * 
 * @author Jason van Zyl
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @deprecated As of 1.4, use {@link XppDom} instead
 */
@Deprecated
public class Xpp3Dom extends XppDom {
    private static final long serialVersionUID = 10400L;

    /**
     * @deprecated As of 1.4, use {@link XppDom} instead
     */
    @Deprecated
    public Xpp3Dom(final String name) {
        super(name);
    }
}
