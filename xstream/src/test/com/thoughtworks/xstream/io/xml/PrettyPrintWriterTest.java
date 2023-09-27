/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2013, 2018, 2023 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.StringWriter;

import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.StreamException;


public class PrettyPrintWriterTest extends AbstractXMLWriterTest {
    private StringWriter buffer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        buffer = new StringWriter();
        writer = new PrettyPrintWriter(buffer, "  ");
    }

    @Override
    protected void assertXmlProducedIs(final String expected) {
        assertEquals(expected, buffer.toString());
    }

    @Override
    public void testSupportsNestedElements() { // Note: This overrides a test in superclass to
        // include indentation

        writer.startNode("hello");
        writer.startNode("world");
        writer.addAttribute("id", "one");

        writer.startNode("one");
        writer.setValue("potato");
        writer.endNode();

        writer.startNode("two");
        writer.addAttribute("id", "two");
        writer.setValue("potatae");
        writer.endNode();

        writer.endNode();

        writer.startNode("empty");
        writer.endNode();

        writer.endNode();

        final String expected = ""
            + "<hello>\n"
            + "  <world id=\"one\">\n"
            + "    <one>potato</one>\n"
            + "    <two id=\"two\">potatae</two>\n"
            + "  </world>\n"
            + "  <empty/>\n"
            + "</hello>";

        assertXmlProducedIs(expected);
    }

    @Override
    public void testAttributesAreResettedForNewNode() { // Note: This overrides a test in
        // superclass to include indentation
        writer.startNode("work");
        writer.startNode("person");
        writer.addAttribute("firstname", "Joe");
        writer.addAttribute("lastname", "Walnes");
        writer.endNode();
        writer.startNode("project");
        writer.addAttribute("XStream", "Codehaus");
        writer.endNode();
        writer.endNode();

        final String expected = ""
            + "<work>\n"
            + "  <person firstname=\"Joe\" lastname=\"Walnes\"/>\n"
            + "  <project XStream=\"Codehaus\"/>\n"
            + "</work>";

        assertXmlProducedIs(expected);
    }

    public void testAllowsUserToOverrideTextAndAttributeEscapingRules() {
        writer = new PrettyPrintWriter(buffer, "  ") {
            @Override
            protected void writeAttributeValue(final QuickWriter writer, final String text) {
                writer.write(replace(text, '&', "_&_"));
            }

            @Override
            protected void writeText(final QuickWriter writer, final String text) {
                writer.write(replace(text, '&', "AND"));
            }
        };

        writer.startNode("evil");
        writer.addAttribute("attr", "hello & stuff");
        writer.setValue("bye & stuff");
        writer.endNode();

        assertXmlProducedIs("<evil attr=\"hello _&_ stuff\">bye AND stuff</evil>");
    }

    public void testSupportsUserDefinedEOL() {
        writer = new PrettyPrintWriter(buffer, "\t") {
            @Override
            protected String getNewLine() {
                return "\r";
            }
        };

        writer.startNode("element");
        writer.startNode("empty");
        writer.endNode();
        writer.endNode();

        assertXmlProducedIs("<element>\r\t<empty/>\r</element>");
    }

    @Override
    public void testSupportsEmptyNestedTags() {
        writer.startNode("parent");
        writer.startNode("child");
        writer.endNode();
        writer.endNode();

        assertXmlProducedIs("<parent>\n  <child/>\n</parent>");
    }

    public void testSupportsNullInQuirksMode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_QUIRKS);
        writer.startNode("tag");
        writer.setValue("\u0000");
        writer.endNode();

        assertXmlProducedIs("<tag>&#x0;</tag>");
    }

    public void testThrowsForNullInXml1_0Mode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_0);
        writer.startNode("tag");
        try {
            writer.setValue("\u0000");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getMessage().indexOf('0') > 0);
        }
    }

    public void testThrowsForNullInXml1_1Mode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_1);
        writer.startNode("tag");
        try {
            writer.setValue("\u0000");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getMessage().indexOf('0') > 0);
        }
    }

    public void testReplacesNullInXml1_0ReplacementMode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_0_REPLACEMENT);
        writer.startNode("tag");
        writer.setValue("\u0000");
        writer.endNode();

        assertXmlProducedIs("<tag>&#xfffd;</tag>");
    }

    public void testReplacesNullInXml1_1ReplacementMode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_1_REPLACEMENT);
        writer.startNode("tag");
        writer.setValue("\u0000");
        writer.endNode();

        assertXmlProducedIs("<tag>&#xfffd;</tag>");
    }

    public void testSupportsOnlyValidControlCharactersInXml1_0Mode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_0);
        writer.startNode("tag");
        final String ctrl = ""
            + "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007"
            + "\u0008\u0009\n\u000b\u000c\r\u000e\u000f"
            + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017"
            + "\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f"
            + "\u007f"
            + "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087"
            + "\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f"
            + "\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097"
            + "\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f"
            + "";
        for (int i = 0; i < ctrl.length(); i++) {
            final char c = ctrl.charAt(i);
            try {
                writer.setValue(new Character(c).toString());
                if (c != '\t' && c != '\n' && c != '\r' && c < '\u007f') {
                    fail("Thrown " + StreamException.class.getName() + " expected");
                }
            } catch (final StreamException e) {
                assertTrue(e.getMessage().indexOf(Integer.toHexString(c)) > 0);
            }
        }
        writer.endNode();
        assertXmlProducedIs("<tag>\t\n&#xd;&#x7f;"
            + "&#x80;&#x81;&#x82;&#x83;&#x84;&#x85;&#x86;&#x87;"
            + "&#x88;&#x89;&#x8a;&#x8b;&#x8c;&#x8d;&#x8e;&#x8f;"
            + "&#x90;&#x91;&#x92;&#x93;&#x94;&#x95;&#x96;&#x97;"
            + "&#x98;&#x99;&#x9a;&#x9b;&#x9c;&#x9d;&#x9e;&#x9f;</tag>");
    }

    public void testSupportsOnlyValidControlCharactersInXml1_1Mode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_1);
        writer.startNode("tag");
        final String ctrl = ""
            + "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007"
            + "\u0008\u0009\n\u000b\u000c\r\u000e\u000f"
            + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017"
            + "\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f"
            + "\u007f"
            + "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087"
            + "\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f"
            + "\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097"
            + "\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f"
            + "";
        for (int i = 0; i < ctrl.length(); i++) {
            final char c = ctrl.charAt(i);
            try {
                writer.setValue(new Character(c).toString());
                if (c == 0) {
                    fail("Thrown " + StreamException.class.getName() + " expected");
                }
            } catch (final StreamException e) {
                assertTrue(e.getMessage().indexOf(Integer.toHexString(c)) > 0);
            }
        }
        writer.endNode();
        assertXmlProducedIs("<tag>&#x1;&#x2;&#x3;&#x4;&#x5;&#x6;&#x7;"
            + "&#x8;\t\n&#xb;&#xc;&#xd;&#xe;&#xf;"
            + "&#x10;&#x11;&#x12;&#x13;&#x14;&#x15;&#x16;&#x17;"
            + "&#x18;&#x19;&#x1a;&#x1b;&#x1c;&#x1d;&#x1e;&#x1f;&#x7f;"
            + "&#x80;&#x81;&#x82;&#x83;&#x84;&#x85;&#x86;&#x87;"
            + "&#x88;&#x89;&#x8a;&#x8b;&#x8c;&#x8d;&#x8e;&#x8f;"
            + "&#x90;&#x91;&#x92;&#x93;&#x94;&#x95;&#x96;&#x97;"
            + "&#x98;&#x99;&#x9a;&#x9b;&#x9c;&#x9d;&#x9e;&#x9f;</tag>");
    }

    public void testReplacesInvalidControlCharactersInXml1_0ReplacementMode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_0_REPLACEMENT);
        writer.startNode("tag");
        final String ctrl = ""
            + "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007"
            + "\u0008\u0009\n\u000b\u000c\r\u000e\u000f"
            + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017"
            + "\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f"
            + "\u007f"
            + "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087"
            + "\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f"
            + "\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097"
            + "\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f"
            + "";
        for (int i = 0; i < ctrl.length(); i++) {
            final char c = ctrl.charAt(i);
            writer.setValue(new Character(c).toString());
        }
        writer.endNode();

        assertXmlProducedIs("<tag>&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;"
            + "&#xfffd;\t\n&#xfffd;&#xfffd;&#xd;&#xfffd;&#xfffd;"
            + "&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;"
            + "&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;&#xfffd;"
            + "&#x7f;"
            + "&#x80;&#x81;&#x82;&#x83;&#x84;&#x85;&#x86;&#x87;"
            + "&#x88;&#x89;&#x8a;&#x8b;&#x8c;&#x8d;&#x8e;&#x8f;"
            + "&#x90;&#x91;&#x92;&#x93;&#x94;&#x95;&#x96;&#x97;"
            + "&#x98;&#x99;&#x9a;&#x9b;&#x9c;&#x9d;&#x9e;&#x9f;</tag>");
    }

    public void testReplacesInvalidControlCharactersInXml1_1ReplacementMode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_1_REPLACEMENT);
        writer.startNode("tag");
        final String ctrl = ""
            + "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007"
            + "\u0008\u0009\n\u000b\u000c\r\u000e\u000f"
            + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017"
            + "\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f"
            + "\u007f"
            + "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087"
            + "\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f"
            + "\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097"
            + "\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f"
            + "";
        for (int i = 0; i < ctrl.length(); i++) {
            final char c = ctrl.charAt(i);
            writer.setValue(new Character(c).toString());
        }
        writer.endNode();
        assertXmlProducedIs("<tag>&#xfffd;&#x1;&#x2;&#x3;&#x4;&#x5;&#x6;&#x7;"
            + "&#x8;\t\n&#xb;&#xc;&#xd;&#xe;&#xf;"
            + "&#x10;&#x11;&#x12;&#x13;&#x14;&#x15;&#x16;&#x17;"
            + "&#x18;&#x19;&#x1a;&#x1b;&#x1c;&#x1d;&#x1e;&#x1f;&#x7f;"
            + "&#x80;&#x81;&#x82;&#x83;&#x84;&#x85;&#x86;&#x87;"
            + "&#x88;&#x89;&#x8a;&#x8b;&#x8c;&#x8d;&#x8e;&#x8f;"
            + "&#x90;&#x91;&#x92;&#x93;&#x94;&#x95;&#x96;&#x97;"
            + "&#x98;&#x99;&#x9a;&#x9b;&#x9c;&#x9d;&#x9e;&#x9f;</tag>");
    }

    public void testSupportsInvalidUnicodeCharacterslInQuirksMode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_QUIRKS);
        writer.startNode("tag");
        final String ctrl = "\ud7ff\ud800\udfff\ue000\ufffd\ufffe\uffff";
        for (int i = 0; i < ctrl.length(); i++) {
            final char c = ctrl.charAt(i);
            writer.setValue(new Character(c).toString());
        }
        writer.endNode();
        assertXmlProducedIs("<tag>&#xd7ff;\ud800\udfff\ue000\ufffd&#xfffe;&#xffff;</tag>");
    }

    public void testThrowsForInvalidUnicodeCharacterslInXml1_0Mode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_0);
        writer.startNode("tag");
        final String ctrl = "\ud7ff\ud800\udfff\ue000\ufffd\ufffe\uffff";
        for (int i = 0; i < ctrl.length(); i++) {
            final char c = ctrl.charAt(i);
            try {
                writer.setValue(new Character(c).toString());
                if (c >= '\ud800' && c < '\udfff' || c == '\ufffe' || c == '\uffff') {
                    fail("Thrown "
                        + StreamException.class.getName()
                        + " for character value "
                        + Integer.toHexString(c)
                        + " expected");
                }
            } catch (final StreamException e) {
                assertTrue(e.getMessage().indexOf(Integer.toHexString(c)) > 0);
            }
        }
        writer.endNode();
        assertXmlProducedIs("<tag>&#xd7ff;\ue000\ufffd</tag>");
    }

    public void testThrowsForInvalidUnicodeCharacterslInXml1_1Mode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_1);
        writer.startNode("tag");
        final String ctrl = "\ud7ff\ud800\udfff\ue000\ufffd\ufffe\uffff";
        for (int i = 0; i < ctrl.length(); i++) {
            final char c = ctrl.charAt(i);
            try {
                writer.setValue(new Character(c).toString());
                if (c >= '\ud800' && c < '\udfff' || c == '\ufffe' || c == '\uffff') {
                    fail("Thrown "
                        + StreamException.class.getName()
                        + " for character value "
                        + Integer.toHexString(c)
                        + " expected");
                }
            } catch (final StreamException e) {
                assertTrue(e.getMessage().indexOf(Integer.toHexString(c)) > 0);
            }
        }
        writer.endNode();
        assertXmlProducedIs("<tag>&#xd7ff;\ue000\ufffd</tag>");
    }

    public void testReplacesInvalidUnicodeCharactersInXml1_0ReplacementMode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_0_REPLACEMENT);
        writer.startNode("tag");
        final String ctrl = "\ud7ff\ud800\udfff\ue000\ufffd\ufffe\uffff";
        for (int i = 0; i < ctrl.length(); i++) {
            final char c = ctrl.charAt(i);
            writer.setValue(new Character(c).toString());
        }
        writer.endNode();
        assertXmlProducedIs("<tag>&#xd7ff;&#xfffd;&#xfffd;\ue000\ufffd&#xfffd;&#xfffd;</tag>");
    }

    public void testReplacesInvalidUnicodeCharactersInXml1_1ReplacementMode() {
        writer = new PrettyPrintWriter(buffer, PrettyPrintWriter.XML_1_1_REPLACEMENT);
        writer.startNode("tag");
        final String ctrl = "\ud7ff\ud800\udfff\ue000\ufffd\ufffe\uffff";
        for (int i = 0; i < ctrl.length(); i++) {
            final char c = ctrl.charAt(i);
            writer.setValue(new Character(c).toString());
        }
        writer.endNode();
        assertXmlProducedIs("<tag>&#xd7ff;&#xfffd;&#xfffd;\ue000\ufffd&#xfffd;&#xfffd;</tag>");
    }

    private String replace(final String in, final char what, final String with) {
        final int pos = in.indexOf(what);
        if (pos == -1) {
            return in;
        } else {
            return in.substring(0, pos) + with + in.substring(pos + 1);
        }
    }
}
