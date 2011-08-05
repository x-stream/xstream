/*
 * Copyright (c) 2008, 2009, 2010, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 17.04.2008 by Joerg Schaible.
 */
package com.thoughtworks.xstream.io.json;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import java.util.Collection;
import java.util.Map;


/**
 * A specialized {@link StaxWriter} that makes usage of internal functionality of Jettison.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3.1
 */
public class JettisonStaxWriter extends StaxWriter {

    private final MappedNamespaceConvention convention;

    /**
     * @since 1.4
     */
    public JettisonStaxWriter(
        QNameMap qnameMap, XMLStreamWriter out, boolean writeEnclosingDocument,
        boolean namespaceRepairingMode, NameCoder nameCoder,
        MappedNamespaceConvention convention) throws XMLStreamException {
        super(qnameMap, out, writeEnclosingDocument, namespaceRepairingMode, nameCoder);
        this.convention = convention;
    }

    /**
     * @deprecated As of 1.4 use
     *             {@link JettisonStaxWriter#JettisonStaxWriter(QNameMap, XMLStreamWriter, boolean, boolean, NameCoder, MappedNamespaceConvention)}
     *             instead
     */
    public JettisonStaxWriter(
        QNameMap qnameMap, XMLStreamWriter out, boolean writeEnclosingDocument,
        boolean namespaceRepairingMode, XmlFriendlyReplacer replacer,
        MappedNamespaceConvention convention) throws XMLStreamException {
        this(qnameMap, out, writeEnclosingDocument, namespaceRepairingMode, (NameCoder) replacer, convention);
    }

    public JettisonStaxWriter(
        QNameMap qnameMap, XMLStreamWriter out, boolean writeEnclosingDocument,
        boolean namespaceRepairingMode, MappedNamespaceConvention convention)
        throws XMLStreamException {
        super(qnameMap, out, writeEnclosingDocument, namespaceRepairingMode);
        this.convention = convention;
    }

    public JettisonStaxWriter(
        QNameMap qnameMap, XMLStreamWriter out, MappedNamespaceConvention convention)
        throws XMLStreamException {
        super(qnameMap, out);
        this.convention = convention;
    }

    /**
     * @since 1.4
     */
    public JettisonStaxWriter(
        QNameMap qnameMap, XMLStreamWriter out, NameCoder nameCoder, MappedNamespaceConvention convention)
        throws XMLStreamException {
        super(qnameMap, out, nameCoder);
        this.convention = convention;
    }

    public void startNode(String name, Class clazz) {
        XMLStreamWriter out = getXMLStreamWriter();
        if (clazz != null && out instanceof AbstractXMLStreamWriter) {
            if (Collection.class.isAssignableFrom(clazz)
                || Map.class.isAssignableFrom(clazz)
                || clazz.isArray()) {
                QName qname = getQNameMap().getQName(encodeNode(name));
                String prefix = qname.getPrefix();
                String uri = qname.getNamespaceURI();
                String key = convention.createKey(prefix, uri, qname.getLocalPart());
                if (!((AbstractXMLStreamWriter)out).getSerializedAsArrays().contains(key)) {
                    // Typo is in the API of Jettison ...
                    ((AbstractXMLStreamWriter)out).seriliazeAsArray(key);
                }
            }
        }
        startNode(name);
    }
}
