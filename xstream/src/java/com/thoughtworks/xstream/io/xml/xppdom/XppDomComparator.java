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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * Comparator for {@link XppDom}. Comparator can trace the XPath where the comparison failed.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.1
 */
public class XppDomComparator implements Comparator {
    private final ThreadLocal xpath;

    /**
     * Creates a new Xpp3DomComparator object.
     * 
     * @since 1.4.1
     */
    public XppDomComparator() {
        this(null);
    }

    /**
     * Creates a new Xpp3DomComparator object with XPath identification.
     * 
     * @param xpath the reference for the XPath
     * @since 1.4.1
     */
    public XppDomComparator(final ThreadLocal xpath) {
        this.xpath = xpath;
    }

    public int compare(final Object dom1, final Object dom2) {

        final StringBuffer xpath = new StringBuffer("/");
        final int s = compareInternal((XppDom)dom1, (XppDom)dom2, xpath, -1);
        if (xpath != null) {
            if (s != 0) {
                this.xpath.set(xpath.toString());
            } else {
                this.xpath.set(null);
            }
        }

        return s;
    }

    private int compareInternal(final XppDom dom1, final XppDom dom2,
        final StringBuffer xpath, final int count) {
        final int pathlen = xpath.length();
        final String name = dom1.getName();
        int s = name.compareTo(dom2.getName());
        xpath.append(name);
        if (count >= 0) {
            xpath.append('[').append(count).append(']');
        }

        if (s != 0) {
            xpath.append('?');

            return s;
        }

        final String[] attributes = dom1.getAttributeNames();
        final String[] attributes2 = dom2.getAttributeNames();
        final int len = attributes.length;
        s = attributes2.length - len;
        if (s != 0) {
            xpath.append("::count(@*)");

            return s < 0 ? 1 : -1;
        }

        Arrays.sort(attributes);
        Arrays.sort(attributes2);
        for (int i = 0; i < len; ++i) {
            final String attribute = attributes[i];
            s = attribute.compareTo(attributes2[i]);
            if (s != 0) {
                xpath.append("[@").append(attribute).append("?]");

                return s;
            }

            s = dom1.getAttribute(attribute).compareTo(dom2.getAttribute(attribute));
            if (s != 0) {
                xpath.append("[@").append(attribute).append(']');

                return s;
            }
        }

        final int children = dom1.getChildCount();
        s = dom2.getChildCount() - children;
        if (s != 0) {
            xpath.append("::count(*)");

            return s < 0 ? 1 : -1;
        }

        if (children > 0) {
            if (dom1.getValue() != null || dom2.getValue() != null) {
                throw new IllegalArgumentException("XppDom cannot handle mixed mode at "
                    + xpath
                    + "::text()");
            }

            xpath.append('/');

            final Map names = new HashMap();
            for (int i = 0; i < children; ++i) {
                final XppDom child1 = dom1.getChild(i);
                final XppDom child2 = dom2.getChild(i);
                final String child = child1.getName();
                if (!names.containsKey(child)) {
                    names.put(child, new int[1]);
                }

                s = compareInternal(child1, child2, xpath, ((int[])names.get(child))[0]++);
                if (s != 0) {
                    return s;
                }
            }
        } else {
            final String value2 = dom2.getValue();
            final String value1 = dom1.getValue();
            if (value1 == null) {
                s = value2 == null ? 0 : -1;
            } else {
                s = value2 == null ? 1 : value1.compareTo(value2);
            }

            if (s != 0) {
                xpath.append("::text()");

                return s;
            }
        }

        xpath.setLength(pathlen);

        return s;
    }
}
