/*
 * Copyright (C) 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 05. May 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.products;

import com.thoughtworks.xstream.io.xml.KXml2DomDriver;

/**
 * Uses XStream with the Xpp3 DOM driver for parsing XML.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see com.thoughtworks.xstream.tools.benchmark.Product
 * @see com.thoughtworks.xstream.XStream
 * @see KXml2DomDriver
 */
public class XStreamKXml2DOM extends XStreamDriver {

    public XStreamKXml2DOM() {
        super(new KXml2DomDriver(), "XML with kXML2 DOM parser");
    }
}
