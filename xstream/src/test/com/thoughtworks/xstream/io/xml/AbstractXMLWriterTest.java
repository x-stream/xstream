/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 05. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import junit.framework.TestCase;

public abstract class AbstractXMLWriterTest extends TestCase {

    protected HierarchicalStreamWriter writer;

    protected abstract void assertXmlProducedIs(String expected);
    
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

    public void testProducesXmlElements() {
        writer.startNode("hello");
        writer.setValue("world");
        writer.endNode();

        assertXmlProducedIs("<hello>world</hello>");
    }

    public void testSupportsNestedElements() {

        writer.startNode("a");

        writer.startNode("b");
        writer.setValue("one");
        writer.endNode();

        writer.startNode("b");
        writer.setValue("two");
        writer.endNode();

        writer.startNode("c");
        writer.startNode("d");
        writer.setValue("three");
        writer.endNode();
        writer.endNode();

        writer.endNode();

        assertXmlProducedIs("<a><b>one</b><b>two</b><c><d>three</d></c></a>");
    }

    public void testSupportsEmptyTags() {
        writer.startNode("empty");
        writer.endNode();

        assertXmlProducedIs("<empty/>");
    }

    public void testSupportsAttributes() {
        writer.startNode("person");
        writer.addAttribute("firstname", "Joe");
        writer.addAttribute("lastname", "Walnes");
        writer.endNode();

        assertXmlProducedIs("<person firstname=\"Joe\" lastname=\"Walnes\"/>");
    }

    public void testAttributesAreResettedForNewNode() {
        writer.startNode("work");
        writer.startNode("person");
        writer.addAttribute("firstname", "Joe");
        writer.addAttribute("lastname", "Walnes");
        writer.endNode();
        writer.startNode("project");
        writer.addAttribute("XStream", "Codehaus");
        writer.endNode();
        writer.endNode();

        assertXmlProducedIs("<work><person firstname=\"Joe\" lastname=\"Walnes\"/><project XStream=\"Codehaus\"/></work>");
    }

    public void testEscapesXmlUnfriendlyCharacters() {
        writer.startNode("evil");
        writer.addAttribute("attr", "w0000 $ <x\"x> &!;");
        writer.setValue("w0000 $ <xx> &!;");
        writer.endNode();

        assertXmlProducedIs("<evil attr=\"w0000 $ &lt;x&quot;x&gt; &amp;!;\">w0000 $ &lt;xx&gt; &amp;!;</evil>");
    }

    public void testEscapesWhitespaceCharacters() {
        writer.startNode("evil");
        writer.setValue("one\ntwo\rthree\r\nfour\n\rfive\tsix");
        writer.endNode();

        assertXmlProducedIs("<evil>one\n"
                + "two&#xd;three&#xd;\n"
                + "four\n"
                + "&#xd;five\tsix</evil>");
    }

    public void testSupportsEmptyNestedTags() {
        writer.startNode("parent");
        writer.startNode("child");
        writer.endNode();
        writer.endNode();

        assertXmlProducedIs("<parent><child/></parent>");
    }
}
