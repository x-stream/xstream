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


public class XppDomWriter extends AbstractDocumentWriter<XppDom, XppDom> {
    public XppDomWriter() {
        this(null, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.2.1
     */
    public XppDomWriter(final XppDom parent) {
        this(parent, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public XppDomWriter(final NameCoder nameCoder) {
        this(null, nameCoder);
    }

    /**
     * @since 1.4
     */
    public XppDomWriter(final XppDom parent, final NameCoder nameCoder) {
        super(parent, nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link XppDomWriter#XppDomWriter(NameCoder)} instead
     */
    @Deprecated
    public XppDomWriter(final XmlFriendlyReplacer replacer) {
        this(null, replacer);
    }

    /**
     * @since 1.2.1
     * @deprecated As of 1.4 use {@link XppDomWriter#XppDomWriter(XppDom, NameCoder)} instead.
     */
    @Deprecated
    public XppDomWriter(final XppDom parent, final XmlFriendlyReplacer replacer) {
        this(parent, (NameCoder)replacer);
    }

    public XppDom getConfiguration() {
        return getTopLevelNodes().get(0);
    }

    @Override
    protected XppDom createNode(final String name) {
        final XppDom newNode = new XppDom(encodeNode(name));
        final XppDom top = top();
        if (top != null) {
            top().addChild(newNode);
        }
        return newNode;
    }

    @Override
    public void setValue(final String text) {
        top().setValue(text);
    }

    @Override
    public void addAttribute(final String key, final String value) {
        top().setAttribute(encodeAttribute(key), value);
    }

    private XppDom top() {
        return getCurrent();
    }
}
