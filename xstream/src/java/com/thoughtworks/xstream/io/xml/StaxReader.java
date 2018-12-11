/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. September 2004 by James Strachan
 */
package com.thoughtworks.xstream.io.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A reader using the StAX API.
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class StaxReader extends AbstractPullReader {

    private final QNameMap qnameMap;
    private final XMLStreamReader in;

    public StaxReader(final QNameMap qnameMap, final XMLStreamReader in) {
        this(qnameMap, in, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public StaxReader(final QNameMap qnameMap, final XMLStreamReader in, final NameCoder replacer) {
        super(replacer);
        this.qnameMap = qnameMap;
        this.in = in;
        moveDown();
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link StaxReader#StaxReader(QNameMap, XMLStreamReader, NameCoder)} instead.
     */
    @Deprecated
    public StaxReader(final QNameMap qnameMap, final XMLStreamReader in, final XmlFriendlyReplacer replacer) {
        this(qnameMap, in, (NameCoder)replacer);
    }

    @Override
    protected int pullNextEvent() {
        try {
            switch (in.next()) {
            case XMLStreamConstants.START_DOCUMENT:
            case XMLStreamConstants.START_ELEMENT:
                return START_NODE;
            case XMLStreamConstants.END_DOCUMENT:
            case XMLStreamConstants.END_ELEMENT:
                return END_NODE;
            case XMLStreamConstants.CDATA:
            case XMLStreamConstants.CHARACTERS:
                return TEXT;
            case XMLStreamConstants.COMMENT:
                return COMMENT;
            default:
                return OTHER;
            }
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    @Override
    protected String pullElementName() {
        // let the QNameMap handle any mapping of QNames to Java class names
        final QName qname = in.getName();
        return qnameMap.getJavaClassName(qname);
    }

    @Override
    protected String pullText() {
        return in.getText();
    }

    @Override
    public String getAttribute(final String name) {
        return in.getAttributeValue(null, encodeAttribute(name));
    }

    @Override
    public String getAttribute(final int index) {
        return in.getAttributeValue(index);
    }

    @Override
    public int getAttributeCount() {
        return in.getAttributeCount();
    }

    @Override
    public String getAttributeName(final int index) {
        return decodeAttribute(in.getAttributeLocalName(index));
    }

    @Override
    public void appendErrors(final ErrorWriter errorWriter) {
        errorWriter.add("line number", String.valueOf(in.getLocation().getLineNumber()));
    }

    @Override
    public void close() {
        try {
            in.close();
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

}
