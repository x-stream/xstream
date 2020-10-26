/*
 * Copyright (C) 2009, 2011, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. May 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml.xppdom;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


/**
 * Simple Document Object Model for XmlPullParser implementations.
 * 
 * @author Jason van Zyl
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class XppDom implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private String value;
    private Map<String, String> attributes;
    private final List<XppDom> childList;
    transient private Map<String, XppDom> childMap;
    private XppDom parent;

    public XppDom(final String name) {
        this.name = name;
        childList = new ArrayList<>();
        childMap = new HashMap<>();
    }

    // ----------------------------------------------------------------------
    // Name handling
    // ----------------------------------------------------------------------

    public String getName() {
        return name;
    }

    // ----------------------------------------------------------------------
    // Value handling
    // ----------------------------------------------------------------------

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    // ----------------------------------------------------------------------
    // Attribute handling
    // ----------------------------------------------------------------------

    public String[] getAttributeNames() {
        if (null == attributes) {
            return new String[0];
        } else {
            return attributes.keySet().toArray(new String[0]);
        }
    }

    public String getAttribute(final String name) {
        return null != attributes ? (String)attributes.get(name) : null;
    }

    public void setAttribute(final String name, final String value) {
        if (null == attributes) {
            attributes = new HashMap<>();
        }

        attributes.put(name, value);
    }

    // ----------------------------------------------------------------------
    // Child handling
    // ----------------------------------------------------------------------

    public XppDom getChild(final int i) {
        return childList.get(i);
    }

    public XppDom getChild(final String name) {
        return childMap.get(name);
    }

    public void addChild(final XppDom xpp3Dom) {
        xpp3Dom.setParent(this);
        childList.add(xpp3Dom);
        childMap.put(xpp3Dom.getName(), xpp3Dom);
    }

    public XppDom[] getChildren() {
        if (null == childList) {
            return new XppDom[0];
        } else {
            return childList.toArray(new XppDom[0]);
        }
    }

    public XppDom[] getChildren(final String name) {
        if (null == childList) {
            return new XppDom[0];
        } else {
            final ArrayList<XppDom> children = new ArrayList<>();
            final int size = childList.size();

            for (int i = 0; i < size; i++) {
                final XppDom configuration = childList.get(i);
                if (name.equals(configuration.getName())) {
                    children.add(configuration);
                }
            }

            return children.toArray(new XppDom[0]);
        }
    }

    public int getChildCount() {
        if (null == childList) {
            return 0;
        }

        return childList.size();
    }

    // ----------------------------------------------------------------------
    // Parent handling
    // ----------------------------------------------------------------------

    public XppDom getParent() {
        return parent;
    }

    public void setParent(final XppDom parent) {
        this.parent = parent;
    }

    // ----------------------------------------------------------------------
    // Serialization
    // ----------------------------------------------------------------------

    Object readResolve() {
        childMap = new HashMap<>();
        for (final XppDom element : childList) {
            childMap.put(element.getName(), element);
        }
        return this;
    }

    // ----------------------------------------------------------------------
    // DOM builder
    // ----------------------------------------------------------------------

    /**
     * Build an XPP DOM hierarchy. The {@link java.io.InputStream} or {@link java.io.Reader} used by the parser must
     * have already been set. The method does not close it after reading the document's end.
     * 
     * @param parser the XPP instance
     * @throws XmlPullParserException if the parser turns into an invalid state or reads invalid XML
     * @throws IOException if the data cannot be read
     */
    public static XppDom build(final XmlPullParser parser) throws XmlPullParserException, IOException {
        final List<XppDom> elements = new ArrayList<>();
        final List<StringBuilder> values = new ArrayList<>();
        XppDom node = null;

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                final String rawName = parser.getName();

                // Use XppDom when deprecated Xpp3Dom is removed
                final XppDom child = new Xpp3Dom(rawName);

                final int depth = elements.size();
                if (depth > 0) {
                    final XppDom parent = elements.get(depth - 1);
                    parent.addChild(child);
                }

                elements.add(child);
                values.add(new StringBuilder());

                final int attributesSize = parser.getAttributeCount();
                for (int i = 0; i < attributesSize; i++) {
                    final String name = parser.getAttributeName(i);
                    final String value = parser.getAttributeValue(i);
                    child.setAttribute(name, value);
                }
            } else if (eventType == XmlPullParser.TEXT) {
                final int depth = values.size() - 1;
                final StringBuilder valueBuffer = values.get(depth);
                valueBuffer.append(parser.getText());
            } else if (eventType == XmlPullParser.END_TAG) {
                final int depth = elements.size() - 1;
                final XppDom finalNode = elements.remove(depth);
                final String accumulatedValue = values.remove(depth).toString();

                String finishedValue;
                if (0 == accumulatedValue.length()) {
                    finishedValue = null;
                } else {
                    finishedValue = accumulatedValue;
                }

                finalNode.setValue(finishedValue);
                if (0 == depth) {
                    node = finalNode;
                }
            }

            eventType = parser.next();
        }

        return node;
    }
}
