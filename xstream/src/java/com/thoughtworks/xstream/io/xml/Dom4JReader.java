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

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;


public class Dom4JReader extends AbstractDocumentReader {

    private Element currentElement;

    /**
     * @since 1.4.11
     */
    public Dom4JReader(final Branch branch) {
        this(branch instanceof Element ? (Element)branch : ((Document)branch).getRootElement());
    }

    public Dom4JReader(final Element rootElement) {
        this(rootElement, new XmlFriendlyNameCoder());
    }

    public Dom4JReader(final Document document) {
        this(document.getRootElement());
    }

    /**
     * @since 1.4
     */
    public Dom4JReader(final Element rootElement, final NameCoder nameCoder) {
        super(rootElement, nameCoder);
    }

    /**
     * @since 1.4
     */
    public Dom4JReader(final Document document, final NameCoder nameCoder) {
        this(document.getRootElement(), nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link Dom4JReader#Dom4JReader(Element, NameCoder)} instead
     */
    @Deprecated
    public Dom4JReader(final Element rootElement, final XmlFriendlyReplacer replacer) {
        this(rootElement, (NameCoder)replacer);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link Dom4JReader#Dom4JReader(Document, NameCoder)} instead
     */
    @Deprecated
    public Dom4JReader(final Document document, final XmlFriendlyReplacer replacer) {
        this(document.getRootElement(), (NameCoder)replacer);
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
        return currentElement.attributeValue(encodeAttribute(name));
    }

    @Override
    public String getAttribute(final int index) {
        return currentElement.attribute(index).getValue();
    }

    @Override
    public int getAttributeCount() {
        return currentElement.attributeCount();
    }

    @Override
    public String getAttributeName(final int index) {
        return decodeAttribute(currentElement.attribute(index).getQualifiedName());
    }

    @Override
    protected Object getParent() {
        return currentElement.getParent();
    }

    @Override
    protected Object getChild(final int index) {
        return currentElement.elements().get(index);
    }

    @Override
    protected int getChildCount() {
        return currentElement.elements().size();
    }

    @Override
    protected void reassignCurrentElement(final Object current) {
        currentElement = (Element)current;
    }

    @Override
    public String peekNextChild() {
        @SuppressWarnings("unchecked")
        final List<Element> list = currentElement.elements();
        if (null == list || list.isEmpty()) {
            return null;
        }
        return decodeNode(list.get(0).getName());
    }

    @Override
    public void appendErrors(final ErrorWriter errorWriter) {
        errorWriter.add("xpath", currentElement.getPath());
    }

}
