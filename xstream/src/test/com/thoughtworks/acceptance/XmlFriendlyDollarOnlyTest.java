/*
 * Copyright (C) 2007 XStream Committers.
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
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;


public class XmlFriendlyDollarOnlyTest extends XmlFriendlyTest {

    protected XStream createXStream() {
        return new XStream(new XppDriver(new XmlFriendlyReplacer("_-", "_")));
    }

    protected Object assertBothWays(Object root, String xml) {
        return super.assertBothWays(root, replaceAll(xml, "__", "_"));
    }
    
    // String.replaceAll is JDK 1.4
    protected String replaceAll(String s, final String occurance, final String replacement) {
        final int len = occurance.length();
        final int inc = len - replacement.length();
        int i = -inc;
        final StringBuffer buff = new StringBuffer(s);
        // StringBuffer has no indexOf in JDK 1.3
        while((i = buff.toString().indexOf(occurance, i + inc)) >= 0) {
            buff.replace(i, i + len, replacement);
        }
        return buff.toString();
    }

}
