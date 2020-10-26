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

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * @author Laurent Bihanic
 */
public class JDomReader extends AbstractDocumentReader {

    private Element currentElement;

    public JDomReader(final Element root) {
        super(root);
    }

    public JDomReader(final Document document) {
        super(document.getRootElement());
    }

    /**
     * @since 1.4
     */
    public JDomReader(final Element root, final NameCoder nameCoder) {
        super(root, nameCoder);
    }

    /**
     * @since 1.4
     */
    public JDomReader(final Document document, final NameCoder nameCoder) {
        super(document.getRootElement(), nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link JDomReader#JDomReader(Element, NameCoder)} instead.
     */
    @Deprecated
    public JDomReader(final Element root, final XmlFriendlyReplacer replacer) {
        this(root, (NameCoder)replacer);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link JDomReader#JDomReader(Document, NameCoder)} instead.
     */
    @Deprecated
    public JDomReader(final Document document, final XmlFriendlyReplacer replacer) {
        this(document.getRootElement(), (NameCoder)replacer);
    }

    @Override
    protected void reassignCurrentElement(final Object current) {
        currentElement = (Element)current;
    }

    @Override
    protected Object getParent() {
        // JDOM 1.0:
        return currentElement.getParentElement();

        // JDOM b10:
        // Parent parent = currentElement.getParent();
        // return (parent instanceof Element) ? (Element)parent : null;

        // JDOM b9 and earlier:
        // return currentElement.getParent();
    }

    @Override
    protected Object getChild(final int index) {
        return currentElement.getChildren().get(index);
    }

    @Override
    protected int getChildCount() {
        return currentElement.getChildren().size();
    }

    @Override
    public String getNodeName() {
        return decodeNode(currentElement.getName());
    }

    @Override
    public String getValue() {
        return currentElement.getText();
    }

    @Override
    public String getAttribute(final String name) {
        return currentElement.getAttributeValue(encodeAttribute(name));
    }

    @Override
    public String getAttribute(final int index) {
        return ((Attribute)currentElement.getAttributes().get(index)).getValue();
    }

    @Override
    public int getAttributeCount() {
        return currentElement.getAttributes().size();
    }

    @Override
    public String getAttributeName(final int index) {
        return decodeAttribute(((Attribute)currentElement.getAttributes().get(index)).getQualifiedName());
    }

    @Override
    public String peekNextChild() {
        @SuppressWarnings("unchecked")
        final List<Element> list = currentElement.getChildren();
        if (null == list || list.isEmpty()) {
            return null;
        }
        return decodeNode(list.get(0).getName());
    }

}
