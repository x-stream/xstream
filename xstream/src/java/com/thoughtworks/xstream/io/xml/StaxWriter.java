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
    private QNameMap qnameMap;
    private XMLStreamWriter out;
    private boolean writeStartDocument;

    public StaxWriter(QNameMap qnameMap, XMLStreamWriter out) throws XMLStreamException {
        this(qnameMap, out, true);
    }

    /**
     * Allows a StaxWriter to be created for partial XML output
     *
     * @param qnameMap           is the mapper of Java class names to QNames
     * @param out                the stream to output to
     * @param writeStartDocument a flag to indicate whether or not the start/end document events should be written
     * @throws XMLStreamException if the events could not be written to the output
     */
    public StaxWriter(QNameMap qnameMap, XMLStreamWriter out, boolean writeStartDocument) throws XMLStreamException {
        this.qnameMap = qnameMap;
        this.out = out;
        this.writeStartDocument = writeStartDocument;
        if (writeStartDocument) {
            out.writeStartDocument();
        }
    }

    /**
     * Call this method when you're finished with me
     */
    public void close() {
        try {
            if (writeStartDocument) {
                out.writeEndDocument();
            }
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
            out.writeEndElement();
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
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }
}
