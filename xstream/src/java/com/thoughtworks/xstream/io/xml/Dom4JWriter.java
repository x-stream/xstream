/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import org.dom4j.Branch;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.thoughtworks.xstream.io.naming.NameCoder;


public class Dom4JWriter extends AbstractDocumentWriter<Branch, Element> {

    private final DocumentFactory documentFactory;

    /**
     * @since 1.4
     */
    public Dom4JWriter(final Branch root, final DocumentFactory factory, final NameCoder nameCoder) {
        super(root, nameCoder);
        documentFactory = factory;
    }

    /**
     * @since 1.4
     */
    public Dom4JWriter(final DocumentFactory factory, final NameCoder nameCoder) {
        this(null, factory, nameCoder);
    }

    /**
     * @since 1.4
     */
    public Dom4JWriter(final Branch root, final NameCoder nameCoder) {
        this(root, new DocumentFactory(), nameCoder);
    }

    /**
     * @since 1.2.1
     * @deprecated As of 1.4 use {@link Dom4JWriter#Dom4JWriter(Branch, DocumentFactory, NameCoder)} instead.
     */
    @Deprecated
    public Dom4JWriter(final Branch root, final DocumentFactory factory, final XmlFriendlyReplacer replacer) {
        this(root, factory, (NameCoder)replacer);
    }

    /**
     * @since 1.2.1
     * @deprecated As of 1.4 use {@link Dom4JWriter#Dom4JWriter(DocumentFactory, NameCoder)} instead.
     */
    @Deprecated
    public Dom4JWriter(final DocumentFactory factory, final XmlFriendlyReplacer replacer) {
        this(null, factory, (NameCoder)replacer);
    }

    /**
     * @since 1.2.1
     */
    public Dom4JWriter(final DocumentFactory documentFactory) {
        this(documentFactory, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.2.1
     * @deprecated As of 1.4 use {@link Dom4JWriter#Dom4JWriter(Branch, NameCoder)} instead
     */
    @Deprecated
    public Dom4JWriter(final Branch root, final XmlFriendlyReplacer replacer) {
        this(root, new DocumentFactory(), (NameCoder)replacer);
    }

    public Dom4JWriter(final Branch root) {
        this(root, new DocumentFactory(), new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.2.1
     */
    public Dom4JWriter() {
        this(new DocumentFactory(), new XmlFriendlyNameCoder());
    }

    @Override
    protected Element createNode(final String name) {
        final Element element = documentFactory.createElement(encodeNode(name));
        final Branch top = top();
        if (top != null) {
            top().add(element);
        }
        return element;
    }

    @Override
    public void setValue(final String text) {
        top().setText(text);
    }

    @Override
    public void addAttribute(final String key, final String value) {
        ((Element)top()).addAttribute(encodeAttribute(key), value);
    }

    private Branch top() {
        return getCurrent();
    }
}
