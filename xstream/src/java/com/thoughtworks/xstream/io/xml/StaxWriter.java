package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * A stream writing that outputs to a StAX stream writer
 *
 * @author James Strachan
 * @version $Revision$
 */
public class StaxWriter implements HierarchicalStreamWriter {
    private XMLStreamWriter out;

    public StaxWriter(XMLStreamWriter out) throws XMLStreamException {
        this.out = out;
        out.writeStartDocument();
    }

    /**
     * Call this method when you're finished with me
     */
    public void close() {
        try {
            out.writeEndDocument();
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
            out.writeStartElement(name);
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }
}
