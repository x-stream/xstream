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

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;


/**
 * @author Jason van Zyl
 */
public class XppDomReader extends AbstractDocumentReader {

    private XppDom currentElement;

    public XppDomReader(final XppDom xppDom) {
        super(xppDom);
    }

    /**
     * @since 1.4
     */
    public XppDomReader(final XppDom xppDom, final NameCoder nameCoder) {
        super(xppDom, nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link XppDomReader#XppDomReader(XppDom, NameCoder)} instead.
     */
    @Deprecated
    public XppDomReader(final XppDom xppDom, final XmlFriendlyReplacer replacer) {
        this(xppDom, (NameCoder)replacer);
    }

    @Override
    public String getNodeName() {
        return decodeNode(currentElement.getName());
    }

    @Override
    public String getValue() {
        String text = null;

        try {
            text = currentElement.getValue();
        } catch (final Exception e) {
            // do nothing.
        }

        return text == null ? "" : text;
    }

    @Override
    public String getAttribute(final String attributeName) {
        return currentElement.getAttribute(encodeAttribute(attributeName));
    }

    @Override
    public String getAttribute(final int index) {
        return currentElement.getAttribute(currentElement.getAttributeNames()[index]);
    }

    @Override
    public int getAttributeCount() {
        return currentElement.getAttributeNames().length;
    }

    @Override
    public String getAttributeName(final int index) {
        return decodeAttribute(currentElement.getAttributeNames()[index]);
    }

    @Override
    protected Object getParent() {
        return currentElement.getParent();
    }

    @Override
    protected Object getChild(final int index) {
        return currentElement.getChild(index);
    }

    @Override
    protected int getChildCount() {
        return currentElement.getChildCount();
    }

    @Override
    protected void reassignCurrentElement(final Object current) {
        currentElement = (XppDom)current;
    }

    @Override
    public String peekNextChild() {
        if (currentElement.getChildCount() == 0) {
            return null;
        }
        return decodeNode(currentElement.getChild(0).getName());
    }

}
