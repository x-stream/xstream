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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A stream writing that outputs to a StAX stream writer
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class StaxWriter extends AbstractXmlWriter {

    private final QNameMap qnameMap;
    private final XMLStreamWriter out;
    private final boolean writeEnclosingDocument;
    private final boolean namespaceRepairingMode;

    private int tagDepth;

    public StaxWriter(final QNameMap qnameMap, final XMLStreamWriter out) throws XMLStreamException {
        this(qnameMap, out, true, true);
    }

    /**
     * Allows a StaxWriter to be created for partial XML output
     * 
     * @param qnameMap is the mapper of Java class names to QNames
     * @param out the stream to output to
     * @param nameCoder the xml-friendly replacer to escape Java names
     * @throws XMLStreamException if the events could not be written to the output
     * @since 1.4
     */
    public StaxWriter(final QNameMap qnameMap, final XMLStreamWriter out, final NameCoder nameCoder)
            throws XMLStreamException {
        this(qnameMap, out, true, true, nameCoder);
    }

    /**
     * Allows a StaxWriter to be created for partial XML output
     * 
     * @param qnameMap is the mapper of Java class names to QNames
     * @param out the stream to output to
     * @param writeEnclosingDocument a flag to indicate whether or not the start/end document events should be written
     * @param namespaceRepairingMode a flag to enable StAX' namespace repairing mode
     * @param nameCoder the xml-friendly replacer to escape Java names
     * @throws XMLStreamException if the events could not be written to the output
     * @since 1.4
     */
    public StaxWriter(
            final QNameMap qnameMap, final XMLStreamWriter out, final boolean writeEnclosingDocument,
            final boolean namespaceRepairingMode, final NameCoder nameCoder) throws XMLStreamException {
        super(nameCoder);
        this.qnameMap = qnameMap;
        this.out = out;
        this.writeEnclosingDocument = writeEnclosingDocument;
        this.namespaceRepairingMode = namespaceRepairingMode;
        if (writeEnclosingDocument) {
            out.writeStartDocument();
        }
    }

    /**
     * Allows a StaxWriter to be created for partial XML output
     * 
     * @param qnameMap is the mapper of Java class names to QNames
     * @param out the stream to output to
     * @param writeEnclosingDocument a flag to indicate whether or not the start/end document events should be written
     * @throws XMLStreamException if the events could not be written to the output
     */
    public StaxWriter(
            final QNameMap qnameMap, final XMLStreamWriter out, final boolean writeEnclosingDocument,
            final boolean namespaceRepairingMode) throws XMLStreamException {
        this(qnameMap, out, writeEnclosingDocument, namespaceRepairingMode, new XmlFriendlyNameCoder());
    }

    /**
     * Allows a StaxWriter to be created for partial XML output
     * 
     * @param qnameMap is the mapper of Java class names to QNames
     * @param out the stream to output to
     * @param writeEnclosingDocument a flag to indicate whether or not the start/end document events should be written
     * @param replacer the xml-friendly replacer to escape Java names
     * @throws XMLStreamException if the events could not be written to the output
     * @since 1.2
     * @deprecated As of 1.4 use {@link StaxWriter#StaxWriter(QNameMap, XMLStreamWriter, boolean, boolean, NameCoder)}
     *             instead
     */
    @Deprecated
    public StaxWriter(
            final QNameMap qnameMap, final XMLStreamWriter out, final boolean writeEnclosingDocument,
            final boolean namespaceRepairingMode, final XmlFriendlyReplacer replacer) throws XMLStreamException {
        this(qnameMap, out, writeEnclosingDocument, namespaceRepairingMode, (NameCoder)replacer);
    }

    @Override
    public void flush() {
        try {
            out.flush();
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    /**
     * Call this method when you're finished with me
     */
    @Override
    public void close() {
        try {
            out.close();
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void addAttribute(final String name, final String value) {
        try {
            out.writeAttribute(encodeAttribute(name), value);
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void endNode() {
        try {
            tagDepth--;
            out.writeEndElement();
            if (tagDepth == 0 && writeEnclosingDocument) {
                out.writeEndDocument();
            }
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void setValue(final String text) {
        try {
            out.writeCharacters(text);
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void startNode(final String name) {
        try {
            final QName qname = qnameMap.getQName(encodeNode(name));
            final String prefix = qname.getPrefix();
            final String uri = qname.getNamespaceURI();

            // before you ask - yes it really is this complicated to output QNames to StAX
            // handling both repair namespace modes :)

            final boolean hasPrefix = prefix != null && prefix.length() > 0;
            final boolean hasURI = uri != null && uri.length() > 0;
            boolean writeNamespace = false;

            if (hasURI) {
                if (hasPrefix) {
                    final String currentNamespace = out.getNamespaceContext().getNamespaceURI(prefix);
                    if (currentNamespace == null || !currentNamespace.equals(uri)) {
                        writeNamespace = true;
                    }
                } else {
                    final String defaultNamespace = out.getNamespaceContext().getNamespaceURI("");
                    if (defaultNamespace == null || !defaultNamespace.equals(uri)) {
                        writeNamespace = true;
                    }
                }
            }

            out.writeStartElement(prefix, qname.getLocalPart(), uri);
            if (hasPrefix) {
                out.setPrefix(prefix, uri);
            } else if (hasURI) {
                if (writeNamespace) {
                    out.setDefaultNamespace(uri);
                }
            }
            if (hasURI && writeNamespace && !isNamespaceRepairingMode()) {
                if (hasPrefix) {
                    out.writeNamespace(prefix, uri);
                } else {
                    out.writeDefaultNamespace(uri);
                }
            }
            tagDepth++;
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    /**
     * Is StAX namespace repairing mode on or off?
     */
    public boolean isNamespaceRepairingMode() {
        return namespaceRepairingMode;
    }

    protected QNameMap getQNameMap() {
        return qnameMap;
    }

    protected XMLStreamWriter getXMLStreamWriter() {
        return out;
    }

}
