package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * A stream writing that outputs to a StAX stream writer
 *
 * @author James Strachan
 * @version $Revision$
 */
public class StaxWriter implements HierarchicalStreamWriter {
    private final QNameMap qnameMap;
    private final XMLStreamWriter out;
    private final boolean writeEnclosingDocument;

    private int tagDepth;

    public StaxWriter(QNameMap qnameMap, XMLStreamWriter out) throws XMLStreamException {
        this(qnameMap, out, true);
    }

    /**
     * Allows a StaxWriter to be created for partial XML output
     *
     * @param qnameMap           is the mapper of Java class names to QNames
     * @param out                the stream to output to
     * @param writeEnclosingDocument a flag to indicate whether or not the start/end document events should be written
     * @throws XMLStreamException if the events could not be written to the output
     */
    public StaxWriter(QNameMap qnameMap, XMLStreamWriter out, boolean writeEnclosingDocument) throws XMLStreamException {
        this.qnameMap = qnameMap;
        this.out = out;
        this.writeEnclosingDocument = writeEnclosingDocument;
        if (writeEnclosingDocument) {
            out.writeStartDocument();
        }
    }

    /**
     * Call this method when you're finished with me
     */
    public void close() {
        try {
            out.close();
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public void addAttribute(String name, String value) {
        try {
            out.writeAttribute(name, value);
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public void endNode() {
        try {
            tagDepth--;
            out.writeEndElement();
            if (tagDepth == 0 && writeEnclosingDocument) {
                out.writeEndDocument();
            }
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public void setValue(String text) {
        try {
            out.writeCharacters(text);
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public void startNode(String name) {
        try {
            QName qname = qnameMap.getQName(name);
            String prefix = qname.getPrefix();
            String uri = qname.getNamespaceURI();
            if (prefix != null && prefix.length() > 0) {
                out.setPrefix(prefix, uri);
            }
            else if (uri != null && uri.length() > 0) {
                out.setDefaultNamespace(uri);
            }
            out.writeStartElement(prefix, qname.getLocalPart(), uri);
            tagDepth++;
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }
}
