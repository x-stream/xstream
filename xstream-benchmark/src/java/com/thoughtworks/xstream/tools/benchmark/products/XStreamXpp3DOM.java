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

import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;

/**
 * Uses XStream with the kXML2 DOM driver for parsing XML.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see com.thoughtworks.xstream.tools.benchmark.Product
 * @see com.thoughtworks.xstream.XStream
 * @see Xpp3DomDriver
 */
public class XStreamXpp3DOM extends XStreamDriver {

    public XStreamXpp3DOM() {
        super(new Xpp3DomDriver(), "XML with Xpp3 DOM parser");
    }
}
