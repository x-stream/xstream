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

import nu.xom.Attribute;
import nu.xom.Element;


public class XomWriter extends AbstractDocumentWriter<Element, Element> {

    /**
     * @since 1.2.1
     */
    public XomWriter() {
        this(null);
    }

    public XomWriter(final Element parentElement) {
        this(parentElement, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public XomWriter(final Element parentElement, final NameCoder nameCoder) {
        super(parentElement, nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link XomWriter#XomWriter(Element, NameCoder)} instead
     */
    @Deprecated
    public XomWriter(final Element parentElement, final XmlFriendlyReplacer replacer) {
        this(parentElement, (NameCoder)replacer);
    }

    @Override
    protected Element createNode(final String name) {
        final Element newNode = new Element(encodeNode(name));
        final Element top = top();
        if (top != null) {
            top().appendChild(newNode);
        }
        return newNode;
    }

    @Override
    public void addAttribute(final String name, final String value) {
        top().addAttribute(new Attribute(encodeAttribute(name), value));
    }

    @Override
    public void setValue(final String text) {
        top().appendChild(text);
    }

    private Element top() {
        return getCurrent();
    }
}
