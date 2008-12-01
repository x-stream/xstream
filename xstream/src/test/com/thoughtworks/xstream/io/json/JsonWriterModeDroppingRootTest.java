/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. November 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.json;

import com.thoughtworks.acceptance.objects.Original;
import com.thoughtworks.acceptance.objects.Replaced;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.Writer;


/**
 * Some of these test cases are taken from example JSON listed at
 * http://www.json.org/example.html
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 */
public class JsonWriterModeDroppingRootTest extends JsonHierarchicalStreamDriverTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.aliasSystemAttribute(null, "class");
        xstream.aliasSystemAttribute(null, "resolves-to");
        xstream.aliasSystemAttribute(null, "defined-in");
    }

    protected JsonHierarchicalStreamDriver createDriver() {
        return new JsonHierarchicalStreamDriver() {

            public HierarchicalStreamWriter createWriter(Writer out) {
                // no root and allow invalid JSON for single values as root object
                return new JsonWriter(out, JsonWriter.DROP_ROOT_MODE);
            }
        };
    }

    protected String normalizeExpectation(final String expected) {
        return super.normalizeExpectation(expected.substring(
            expected.indexOf(": ") + 2, expected.length() - 1));
    }

    public void testCanMarshalSets() {
        // This from http://www.json.org/example.html
        xstream.alias("menu", MenuWithSet.class);
        xstream.alias("menuitem", MenuItem.class);

        final MenuWithSet menu = new MenuWithSet();

        final String json = xstream.toXML(menu);
        assertTrue(json.startsWith(normalizeExpectation(expectedMenuStart)));
        assertTrue(json.indexOf(expectedNew.replace('\'', '"')) > 0);
        assertTrue(json.indexOf(expectedOpen.replace('\'', '"')) > 0);
        assertTrue(json.indexOf(expectedClose.replace('\'', '"')) > 0);
        assertTrue(json.endsWith(expectedMenuEnd.replace('\'', '"').substring(
            0, expectedMenuEnd.length() - 1)));
    }

    public void testBracesAndSquareBracketsAreNotEscaped() {
        final String expected = ("" // 
            + "[\n"
            + "  '..{}[],,'\n"
            + "]").replace('\'', '"');
        assertEquals(expected, xstream.toXML(new String[]{"..{}[],,"}));
    }

    public void testWillWriteTagValueAsDefaultValueIfNecessary() {
        xstream.alias("sa", SystemAttributes.class);
        xstream.alias("original", Original.class);
        xstream.alias("replaced", Replaced.class);

        SystemAttributes sa = new SystemAttributes();
        sa.name = "joe";
        sa.object = "walnes";
        sa.original = new Original("hello world");

        String expected = normalizeExpectation(""
            + "{'sa': {\n"
            + "  'name': 'joe',\n"
            + "  'object': 'walnes',\n"
            + "  'original': {\n"
            + "    'replacedValue': 'HELLO WORLD'\n"
            + "  }\n"
            + "}}");

        assertEquals(expected, xstream.toXML(sa));
    }

    public void testStrictJSON() {
        xstream = new XStream(new JsonHierarchicalStreamDriver() {

            public HierarchicalStreamWriter createWriter(Writer out) {
                // do not alow invalid JSON
                return new JsonWriter(out, JsonWriter.DROP_ROOT_MODE | JsonWriter.STRICT_MODE);
            }
        });
        try {
            xstream.toXML(new Integer(123));
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            // OK
        }
    }
}
