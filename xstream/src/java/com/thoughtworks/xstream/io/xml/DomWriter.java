package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Michael Kopp
 */
public class DomWriter implements HierarchicalStreamWriter {
    private final Document document;
    private Element current;

    public DomWriter(Document document) {
        this.document = document;
        this.current = document.getDocumentElement();
    }

    public DomWriter(Element rootElement) {
        document = rootElement.getOwnerDocument();
        current = rootElement;
    }

    public void startNode(String name) {
        final Element child = document.createElement(name);
        if (current == null) {
            document.appendChild(child);
        } else {
            current.appendChild(child);
        }
        current = child;
    }

    public void addAttribute(String name, String value) {
        current.setAttribute(name, value);
    }

    public void setValue(String text) {
        current.appendChild(document.createTextNode(text));
    }

    public void endNode() {
        Node parent = current.getParentNode();
        current = parent instanceof Element ? (Element)parent : null;
    }

    public void flush() {
        // don't need to do anything
    }

    public void close() {
        // don't need to do anything
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }
}