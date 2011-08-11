/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 11. August 2011 by Joerg Schaible.
 */

package com.thoughtworks.xstream.io.xml.xppdom;

import java.util.Comparator;

import com.thoughtworks.xstream.io.xml.xppdom.XppDom;
import com.thoughtworks.xstream.io.xml.xppdom.XppFactory;

import junit.framework.TestCase;


/**
 * Tests {@link XppDomComparator}.
 * 
 * @author J&ouml;rg Schaible
 */
public class XppDomComparatorTest extends TestCase {
    // ~ Instance fields --------------------------------------------------------

    private ThreadLocal xpath;
    private XppDomComparator comparator;

    // ~ Methods ----------------------------------------------------------------

    protected void setUp() throws Exception {
        super.setUp();
        xpath = new ThreadLocal();
        comparator = new XppDomComparator(xpath);
    }

    private void assertEquals(Comparator comparator, Object o1, Object o2) {
        if (comparator.compare(o1, o2) != 0) {
            fail("Cpmarator claims '" + o1 + "' to be different from '" + o2 + "'");
        }
    }

    /**
     * Tests comparison of empty document.
     * 
     * @throws Exception unexpected
     */
    public void testEqualsEmptyDocuments() throws Exception {
        final String xml = "<dom/>";
        XppDom dom1 = XppFactory.buildDom(xml);
        XppDom dom2 = XppFactory.buildDom(xml);
        assertEquals(comparator, dom1, dom2);
        assertNull(xpath.get());
    }

    /**
     * Tests comparison of different values.
     * 
     * @throws Exception unexpected
     */
    public void testSortsElementsWithDifferentValue() throws Exception {
        XppDom dom1 = XppFactory.buildDom("<dom>value1</dom>");
        XppDom dom2 = XppFactory.buildDom("<dom>value2</dom>");
        assertEquals(-1, comparator.compare(dom1, dom2));
        assertEquals("/dom::text()", xpath.get());
        assertEquals(1, comparator.compare(dom2, dom1));
        assertEquals("/dom::text()", xpath.get());
    }

    /**
     * Tests comparison of a value and null.
     * 
     * @throws Exception unexpected
     */
    public void testSortsElementsWithValueAndNull() throws Exception {
        XppDom dom1 = XppFactory.buildDom("<dom/>");
        XppDom dom2 = XppFactory.buildDom("<dom>value</dom>");
        assertEquals(-1, comparator.compare(dom1, dom2));
        assertEquals("/dom::text()", xpath.get());
        assertEquals(1, comparator.compare(dom2, dom1));
        assertEquals("/dom::text()", xpath.get());
    }

    /**
     * Tests comparison of attributes.
     * 
     * @throws Exception unexpected
     */
    public void testEqualsAttributes() throws Exception {
        final String xml = "<dom a='1' b='2'/>";
        XppDom dom1 = XppFactory.buildDom(xml);
        XppDom dom2 = XppFactory.buildDom(xml);
        assertEquals(comparator, dom1, dom2);
        assertNull(xpath.get());
    }

    /**
     * Tests comparison of attributes in different order.
     * 
     * @throws Exception unexpected
     */
    public void testEqualsAttributesInDifferentOrder() throws Exception {
        XppDom dom1 = XppFactory.buildDom("<dom a='1' b='2'/>");
        XppDom dom2 = XppFactory.buildDom("<dom b='2' a='1'/>");
        assertEquals(comparator, dom1, dom2);
        assertNull(xpath.get());
    }

    /**
     * Tests comparison of same attributes with different values.
     * 
     * @throws Exception unexpected
     */
    public void testSortsSameAttributes() throws Exception {
        XppDom dom1 = XppFactory.buildDom("<dom a='1' b='2'/>");
        XppDom dom2 = XppFactory.buildDom("<dom a='2' b='1'/>");
        assertEquals(-1, comparator.compare(dom1, dom2));
        assertEquals("/dom[@a]", xpath.get());
        assertEquals(1, comparator.compare(dom2, dom1));
        assertEquals("/dom[@a]", xpath.get());
    }

    /**
     * Tests comparison of different attributes.
     * 
     * @throws Exception unexpected
     */
    public void testSortsDifferentAttributes() throws Exception {
        XppDom dom1 = XppFactory.buildDom("<dom a='1'/>");
        XppDom dom2 = XppFactory.buildDom("<dom b='1'/>");
        assertEquals(-1, comparator.compare(dom1, dom2));
        assertEquals("/dom[@a?]", xpath.get());
        assertEquals(1, comparator.compare(dom2, dom1));
        assertEquals("/dom[@b?]", xpath.get());
    }

    /**
     * Tests comparison of different number of attributes.
     * 
     * @throws Exception unexpected
     */
    public void testSortsAccordingNumberOfAttributes() throws Exception {
        XppDom dom1 = XppFactory.buildDom("<dom/>");
        XppDom dom2 = XppFactory.buildDom("<dom a='1'/>");
        assertEquals(-1, comparator.compare(dom1, dom2));
        assertEquals("/dom::count(@*)", xpath.get());
        assertEquals(1, comparator.compare(dom2, dom1));
        assertEquals("/dom::count(@*)", xpath.get());
    }

    /**
     * Tests comparison of document with children.
     * 
     * @throws Exception unexpected
     */
    public void testEqualsDocumentsWithChildren() throws Exception {
        final String xml = "<dom><a/></dom>";
        XppDom dom1 = XppFactory.buildDom(xml);
        XppDom dom2 = XppFactory.buildDom(xml);
        assertEquals(comparator, dom1, dom2);
        assertNull(xpath.get());
    }

    /**
     * Tests comparison of different number of children.
     * 
     * @throws Exception unexpected
     */
    public void testSortsAccordingNumberOfChildren() throws Exception {
        XppDom dom1 = XppFactory.buildDom("<dom/>");
        XppDom dom2 = XppFactory.buildDom("<dom><a/></dom>");
        assertEquals(-1, comparator.compare(dom1, dom2));
        assertEquals("/dom::count(*)", xpath.get());
        assertEquals(1, comparator.compare(dom2, dom1));
        assertEquals("/dom::count(*)", xpath.get());
    }

    /**
     * Tests comparison of different elements.
     * 
     * @throws Exception unexpected
     */
    public void testSortsElementsByName() throws Exception {
        XppDom dom1 = XppFactory.buildDom("<dom><a/></dom>");
        XppDom dom2 = XppFactory.buildDom("<dom><b/></dom>");
        assertEquals(-1, comparator.compare(dom1, dom2));
        assertEquals("/dom/a[0]?", xpath.get());
        assertEquals(1, comparator.compare(dom2, dom1));
        assertEquals("/dom/b[0]?", xpath.get());
    }

    /**
     * Tests comparison of different nth elements.
     * 
     * @throws Exception unexpected
     */
    public void testSortsElementsByNthName() throws Exception {
        XppDom dom1 = XppFactory.buildDom("<dom><a/><b/><c/><a/></dom>");
        XppDom dom2 = XppFactory.buildDom("<dom><a/><b/><c/><b/></dom>");
        assertEquals(-1, comparator.compare(dom1, dom2));
        assertEquals("/dom/a[1]?", xpath.get());
        assertEquals(1, comparator.compare(dom2, dom1));
        assertEquals("/dom/b[1]?", xpath.get());
    }

    /**
     * Tests comparison sorts attributes before elements.
     * 
     * @throws Exception unexpected
     */
    public void testSortsAttributesBeforeElements() throws Exception {
        XppDom dom1 = XppFactory.buildDom("<dom x='a'><a/></dom>");
        XppDom dom2 = XppFactory.buildDom("<dom x='b'><b/></dom>");
        assertEquals(-1, comparator.compare(dom1, dom2));
        assertEquals("/dom[@x]", xpath.get());
        assertEquals(1, comparator.compare(dom2, dom1));
        assertEquals("/dom[@x]", xpath.get());
    }

    /**
     * Tests comparison will reset XPath after recursion.
     * 
     * @throws Exception unexpected
     */
    public void testWillResetXPathAfterRecursion() throws Exception {
        XppDom dom1 = XppFactory.buildDom("<dom><a><b>foo</b></a><c x='1'/></dom>");
        XppDom dom2 = XppFactory.buildDom("<dom><a><b>foo</b></a><c x='2'/></dom>");
        assertEquals(-1, comparator.compare(dom1, dom2));
        assertEquals("/dom/c[0][@x]", xpath.get());
        assertEquals(1, comparator.compare(dom2, dom1));
        assertEquals("/dom/c[0][@x]", xpath.get());
    }
}
