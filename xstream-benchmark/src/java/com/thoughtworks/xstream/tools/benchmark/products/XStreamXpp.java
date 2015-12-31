/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. July 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.tools.benchmark.products;

import com.thoughtworks.xstream.io.xml.XppDriver;


/**
 * Uses XStream with the default XPP driver for parsing XML.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see com.thoughtworks.xstream.tools.benchmark.Product
 * @see com.thoughtworks.xstream.XStream
 * @see XppDriver
 * @deprecated As of 1.4.9 use JMH instead
 */
public class XStreamXpp extends XStreamDriver {

    public XStreamXpp() {
        super(new XppDriver(), "XML with default XPP parser");
    }

}
