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

package com.thoughtworks.xstream.io.json;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.acceptance.objects.Original;
import com.thoughtworks.acceptance.objects.Replaced;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Some of these test cases are taken from example JSON listed at http://www.json.org/example.html
 *
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 */
public class JsonWriterModeDroppingRootTest extends JsonHierarchicalStreamDriverTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.aliasSystemAttribute(null, "class");
        xstream.aliasSystemAttribute(null, "resolves-to");
        xstream.aliasSystemAttribute(null, "defined-in");
    }

    @Override
    protected JsonHierarchicalStreamDriver createDriver() {
        return new JsonHierarchicalStreamDriver() {

            @SuppressWarnings("static-access")
            @Override
            public HierarchicalStreamWriter createWriter(final Writer out) {
                // no root and allow invalid JSON for single values as root object
                return new JsonWriter(out, JsonWriter.DROP_ROOT_MODE);
            }
        };
    }

    @Override
    protected String normalizeExpectation(final String expected) {
        return super.normalizeExpectation(expected.substring(expected.indexOf(": ") + 2, expected.length() - 1));
    }

    @Override
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
        assertTrue(json.endsWith(expectedMenuEnd.replace('\'', '"').substring(0, expectedMenuEnd.length() - 1)));
    }

    @Override
    public void testBracesAndSquareBracketsAreNotEscaped() {
        final String expected = ("" //
            + "[\n"
            + "  '..{}[],,'\n"
            + "]").replace('\'', '"');
        assertEquals(expected, xstream.toXML(new String[]{"..{}[],,"}));
    }

    @Override
    public void testWillWriteTagValueAsDefaultValueIfNecessary() {
        xstream.alias("sa", SystemAttributes.class);
        xstream.alias("original", Original.class);
        xstream.alias("replaced", Replaced.class);

        final SystemAttributes sa = new SystemAttributes();
        sa.name = "joe";
        sa.object = "walnes";
        sa.original = new Original("hello world");

        final String expected = normalizeExpectation(""
            + "{'sa': {\n"
            + "  'name': 'joe',\n"
            + "  'object': 'walnes',\n"
            + "  'original': {\n"
            + "    'replacedValue': 'HELLO WORLD'\n"
            + "  }\n"
            + "}}");

        assertEquals(expected, xstream.toXML(sa));
    }

    @Override
    public void testRealTypeIsHonoredWhenWritingTheValue() {
        xstream.alias("sa", SystemAttributes.class);

        final List<String> list = new ArrayList<String>();
        list.add("joe");
        list.add("mauro");
        final SystemAttributes[] sa = new SystemAttributes[2];
        sa[0] = new SystemAttributes();
        sa[0].name = "year";
        sa[0].object = new Integer(2000);
        sa[1] = new SystemAttributes();
        sa[1].name = "names";
        sa[1].object = list;

        final String expected = normalizeExpectation(""
            + "{'sa-array': [\n"
            + "  {\n"
            + "    'name': 'year',\n"
            + "    'object': 2000\n"
            + "  },\n"
            + "  {\n"
            + "    'name': 'names',\n"
            + "    'object': [\n"
            + "      'joe',\n"
            + "      'mauro'\n"
            + "    ]\n"
            + "  }\n"
            + "]}");

        assertEquals(expected, xstream.toXML(sa));
    }

    public void testStrictJSON() {
        xstream = new XStream(new JsonHierarchicalStreamDriver() {

            @SuppressWarnings("static-access")
            @Override
            public HierarchicalStreamWriter createWriter(final Writer out) {
                // do not allow invalid JSON
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
