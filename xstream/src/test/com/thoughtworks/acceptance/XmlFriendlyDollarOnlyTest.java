/*
 * Copyright (C) 2007, 2014, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. September 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.DefaultDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;


public class XmlFriendlyDollarOnlyTest extends XmlFriendlyTest {

    @Override
    protected XStream createXStream() {
        final XStream xstream = new XStream(DefaultDriver.create(new XmlFriendlyNameCoder("_-", "_")));
        setupSecurity(xstream);
        xstream.allowTypesByWildcard(getClass().getSuperclass().getName() + "$*");
        return xstream;
    }

    @Override
    protected <T> T assertBothWays(final Object root, final String xml) {
        return super.assertBothWays(root, xml.replaceAll("__", "_"));
    }
}
