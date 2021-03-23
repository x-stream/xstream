/*
 * Copyright (C) 2019, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17. March 2019 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.MXParserDriver;


/**
 * The factory for the default driver used by XStream.
 * <p>
 * The main purpose of the class is an internal switch of the default driver for testing purposes.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public final class DefaultDriver {
    public static HierarchicalStreamDriver create() {
        return new MXParserDriver();
    }
    public static HierarchicalStreamDriver create(NameCoder coder) {
        return new MXParserDriver(coder);
    }
}
