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

import com.thoughtworks.xstream.converters.ConversionException;


/**
 * Some of these test cases are taken from example JSON listed at
 * http://www.json.org/example.html
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 */
public class MinimalJsonHierarchicalStreamDriverTest extends JsonHierarchicalStreamDriverTest {

    protected boolean usesRoot() {
        return false;
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
        assertTrue(json.endsWith(expectedMenuEnd.replace('\'', '"').substring(0, expectedMenuEnd.length()-1)));
    }

    public void testDoesEscapeValuesAccordingRfc4627() {
        try {
            String expected = normalizeExpectation("{'string': '\\u0000\\u0001\\u001f \uffee'}");
            assertEquals(expected, xstream.toXML("\u0000\u0001\u001f\u0020\uffee"));
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            // OK
        }
    }

    public void testSimpleInteger() {
        try {
            xstream.toXML(new Integer(123));
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            // OK
        }
    }

    public void testBracesAndSquareBracketsAreNotEscaped() {
        final String expected = ("" // 
            + "[\n"
            + "  '..{}[],,'\n"
            + "]").replace('\'', '"');
        assertEquals(expected, xstream.toXML(new String[]{"..{}[],,"}));
    }
}
