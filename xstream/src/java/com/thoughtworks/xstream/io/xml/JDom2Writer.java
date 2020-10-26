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

import org.jdom2.DefaultJDOMFactory;
import org.jdom2.Element;
import org.jdom2.JDOMFactory;

import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * @since 1.4.5
 */
public class JDom2Writer extends AbstractDocumentWriter<Element, Element> {

    private final JDOMFactory documentFactory;

    /**
     * @since 1.4.5
     */
    public JDom2Writer(final Element container, final JDOMFactory factory, final NameCoder nameCoder) {
        super(container, nameCoder);
        documentFactory = factory;
    }

    /**
     * @since 1.4.5
     */
    public JDom2Writer(final Element container, final JDOMFactory factory) {
        this(container, factory, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4.5
     */
    public JDom2Writer(final JDOMFactory factory, final NameCoder nameCoder) {
        this(null, factory, nameCoder);
    }

    /**
     * @since 1.4.5
     */
    public JDom2Writer(final JDOMFactory factory) {
        this(null, factory);
    }

    /**
     * @since 1.4.5
     */
    public JDom2Writer(final Element container, final NameCoder nameCoder) {
        this(container, new DefaultJDOMFactory(), nameCoder);
    }

    /**
     * @since 1.4.5
     */
    public JDom2Writer(final Element container) {
        this(container, new DefaultJDOMFactory());
    }

    /**
     * @since 1.4.5
     */
    public JDom2Writer() {
        this(new DefaultJDOMFactory());
    }

    @Override
    protected Element createNode(final String name) {
        final Element element = documentFactory.element(encodeNode(name));
        final Element parent = top();
        if (parent != null) {
            parent.addContent(element);
        }
        return element;
    }

    @Override
    public void setValue(final String text) {
        top().addContent(documentFactory.text(text));
    }

    @Override
    public void addAttribute(final String key, final String value) {
        top().setAttribute(documentFactory.attribute(encodeAttribute(key), value));
    }

    /**
     * @since 1.4.5
     */
    private Element top() {
        return getCurrent();
    }
}
