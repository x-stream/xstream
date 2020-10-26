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
