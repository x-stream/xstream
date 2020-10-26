/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;


public abstract class AbstractXMLReaderTest extends AbstractReaderTest {

    public static String XML_1_1_HEADER = "<?xml version=\"1.1\"?>";

    public void testChildTagsCanBeMixedWithOtherNodes() throws Exception {
        final HierarchicalStreamReader xmlReader = createReader(
            "<!-- xx --><a> <hello/> <!-- x --> getValue <world/></a>");

        assertTrue(xmlReader.hasMoreChildren());
        xmlReader.moveDown();
        assertEquals("hello", xmlReader.getNodeName());
        xmlReader.moveUp();

        assertTrue(xmlReader.hasMoreChildren());
        xmlReader.moveDown();
        assertEquals("world", xmlReader.getNodeName());
        xmlReader.moveUp();

        assertFalse(xmlReader.hasMoreChildren());
        xmlReader.close();
    }

    public void testTextCanBeExtractedFromTag() throws Exception {
        final HierarchicalStreamReader xmlReader = createReader(
            "<root><a>some<!-- ignore me --> getValue!</a><b><![CDATA[more&&more;]]></b></root>");

        xmlReader.moveDown();
        assertEquals("some getValue!", xmlReader.getValue());
        xmlReader.moveUp();

        xmlReader.moveDown();
        assertEquals("more&&more;", xmlReader.getValue());
        xmlReader.moveUp();
        xmlReader.close();
    }

    public void testReturnsEmptyStringForEmptyTags() throws Exception {
        final HierarchicalStreamReader xmlReader = createReader("<root></root>");

        final String text = xmlReader.getValue();
        assertNotNull(text);
        assertEquals("", text);
        xmlReader.close();
    }

    public void testCanReadCDATAWithEmbeddedTags() throws Exception {
        final String content = "<tag>the content</tag>";
        final HierarchicalStreamReader xmlReader = createReader("<string><![CDATA[" + content + "]]></string>");
        assertEquals(content, xmlReader.getValue());
        xmlReader.close();
    }

    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        final HierarchicalStreamReader xmlReader = createReader(""
            + "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE root [\n"
            + "<!ELEMENT string (#PCDATA)>\n"
            + "<!ENTITY content SYSTEM \"file:src/test/$Package.java\">\n"
            // +"<!ENTITY content SYSTEM \"file:pom.xml\">\n"
            // +"<!ENTITY content SYSTEM \"file:/etc/passwd\">\n"
            + "]><string>&content;</string>");
        assertEquals("", xmlReader.getValue());
        xmlReader.close();
    }

    public void testIsXXEVulnerableWithExternalParameterEntity() throws Exception {
        final HierarchicalStreamReader xmlReader = createReader(""
            + "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE root [\n"
            + "<!ELEMENT string (#PCDATA)>\n"
            + "<!ENTITY % content SYSTEM \"file:src/test/$Package.java\">\n"
            // +"<!ENTITY % content SYSTEM \"file:pom.xml\">\n"
            // +"<!ENTITY % content SYSTEM \"file:/etc/passwd\">\n"
            + "%content;\n"
            + "]><string>test</string>");
        assertEquals("test", xmlReader.getValue());
        xmlReader.close();
    }

    // valid chars of Java names: sharp s, auml, ash, omega, cyrillic D, runic W, euro
    private final static String specialCharsInJavaNames = "\u00df\u00e4\u00e6\u03a9\u0414\u16a5\u20ac";

    protected String getSpecialCharsInJavaNamesForXml10() {
        return specialCharsInJavaNames;
    }

    protected String getSpecialCharsInJavaNamesForXml11() {
        return specialCharsInJavaNames;
    }

    protected final String getSpecialCharsInJavaNamesForXml10_4th() {
        return specialCharsInJavaNames.substring(0, specialCharsInJavaNames.length() - 2);
    }

    public void testSupportsFieldsWithSpecialChars() throws Exception {
        final StringBuilder sb = new StringBuilder();
        final String specialCharsInJavaNames = getSpecialCharsInJavaNamesForXml10();
        for (final char c : specialCharsInJavaNames.toCharArray()) {
            try (final HierarchicalStreamReader xmlReader = createReader(String.format("<%c>Yes</%c>", c, c))) {
                assertEquals("Yes", xmlReader.getValue());
                sb.append(c);
            } catch (final Exception e) {
                ; // failed
            }
        }
        assertEquals(specialCharsInJavaNames, sb.toString());
    }

    public void testSupportsFieldsWithSpecialCharsInXml11() throws Exception {
        final StringBuilder sb = new StringBuilder();
        final String specialCharsInJavaNames = getSpecialCharsInJavaNamesForXml11();
        for (final char c : specialCharsInJavaNames.toCharArray()) {
            try (final HierarchicalStreamReader xmlReader = createReader(String
                .format("%s<_%c>Yes</_%c>", XML_1_1_HEADER, c, c))) {
                assertEquals("Yes", xmlReader.getValue());
                sb.append(c);
            } catch (final Exception e) {
                ; // failed
            }
        }
        assertEquals(specialCharsInJavaNames, sb.toString());
    }

    public void testNonUnicodeCharacterInValue() throws Exception {
        final HierarchicalStreamReader xmlReader = createReader("<string>&#xffff;</string>");
        assertEquals("\uffff", xmlReader.getValue());
        xmlReader.close();
    }

    public void testNonUnicodeCharacterInCDATA() throws Exception {
        final String content = "\uffff";
        final HierarchicalStreamReader xmlReader = createReader("<string><![CDATA[" + content + "]]></string>");
        assertEquals(content, xmlReader.getValue());
        xmlReader.close();
    }

    public void testISOControlCharactersInValue() throws Exception {
        final HierarchicalStreamReader xmlReader = createReader("<string>hello&#x4;-&#x96;world</string>");
        assertEquals("hello\u0004-\u0096world", xmlReader.getValue());
        xmlReader.close();
    }
    
    // inherits tests from superclass
}
