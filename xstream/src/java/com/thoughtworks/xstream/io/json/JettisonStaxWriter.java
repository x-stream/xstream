/*
 * Copyright (c) 2008, 2009, 2010, 2011, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17.04.2008 by Joerg Schaible.
 */
package com.thoughtworks.xstream.io.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;


/**
 * A specialized {@link StaxWriter} that makes usage of internal functionality of Jettison.
 *
 * @author J&ouml;rg Schaible
 * @since 1.3.1
 */
public class JettisonStaxWriter extends StaxWriter {

    private final MappedNamespaceConvention convention;
    private final List stack = new ArrayList();

    /**
     * @since 1.4
     */
    public JettisonStaxWriter(
            final QNameMap qnameMap, final XMLStreamWriter out, final boolean writeEnclosingDocument,
            final boolean namespaceRepairingMode, final NameCoder nameCoder, final MappedNamespaceConvention convention)
            throws XMLStreamException {
        super(qnameMap, out, writeEnclosingDocument, namespaceRepairingMode, nameCoder);
        this.convention = convention;
    }

    /**
     * @deprecated As of 1.4 use
     *             {@link JettisonStaxWriter#JettisonStaxWriter(QNameMap, XMLStreamWriter, boolean, boolean, NameCoder, MappedNamespaceConvention)}
     *             instead
     */
    public JettisonStaxWriter(
            final QNameMap qnameMap, final XMLStreamWriter out, final boolean writeEnclosingDocument,
            final boolean namespaceRepairingMode, final XmlFriendlyReplacer replacer,
            final MappedNamespaceConvention convention)
            throws XMLStreamException {
        this(qnameMap, out, writeEnclosingDocument, namespaceRepairingMode, (NameCoder)replacer, convention);
    }

    public JettisonStaxWriter(
            final QNameMap qnameMap, final XMLStreamWriter out, final boolean writeEnclosingDocument,
            final boolean namespaceRepairingMode, final MappedNamespaceConvention convention)
            throws XMLStreamException {
        super(qnameMap, out, writeEnclosingDocument, namespaceRepairingMode);
        this.convention = convention;
    }

    public JettisonStaxWriter(
            final QNameMap qnameMap, final XMLStreamWriter out, final MappedNamespaceConvention convention)
            throws XMLStreamException {
        super(qnameMap, out);
        this.convention = convention;
    }

    /**
     * @since 1.4
     */
    public JettisonStaxWriter(
            final QNameMap qnameMap, final XMLStreamWriter out, final NameCoder nameCoder,
            final MappedNamespaceConvention convention)
            throws XMLStreamException {
        super(qnameMap, out, nameCoder);
        this.convention = convention;
    }

    public void startNode(final String name, final Class clazz) {
        final XMLStreamWriter out = getXMLStreamWriter();
        String key = "";
        if (clazz != null && out instanceof AbstractXMLStreamWriter) {
            if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) || clazz.isArray()) {
                final QName qname = getQNameMap().getQName(encodeNode(name));
                final String prefix = qname.getPrefix();
                final String uri = qname.getNamespaceURI();
                key = convention.createKey(prefix, uri, qname.getLocalPart());

                if (!JVM.is15()) {
                    final ArrayList serializedAsArrays = ((AbstractXMLStreamWriter)out).getSerializedAsArrays();
                    if (!serializedAsArrays.contains(key)) {
                        serializedAsArrays.add(key);
                    }
                    key = "";
                }
            }
        }
        stack.add(key);
        super.startNode(name);
    }

    public void startNode(final String name) {
        startNode(name, null);
    }

    public void endNode() {
        final String key = (String)stack.remove(stack.size() - 1);
        if (key.length() == 0) {
            super.endNode();
        } else {
            final XMLStreamWriter out = getXMLStreamWriter();
            final ArrayList serializedAsArrays = ((AbstractXMLStreamWriter)out).getSerializedAsArrays();
            serializedAsArrays.add(key);
            super.endNode();
            serializedAsArrays.remove(key);
        }
    }
}
