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
        while((i = buff.indexOf(occurance, i + inc)) >= 0) {
            buff.replace(i, i + len, replacement);
        }
        return buff.toString();
    }

}
