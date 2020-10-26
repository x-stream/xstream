/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
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
