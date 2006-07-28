package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Michael Kopp
 */
public class DomWriter extends AbstractXmlWriter {
    
    private final Document document;
    private Element current;

    public DomWriter(Document document) {
        this(document, new XmlFriendlyReplacer());
    }

    public DomWriter(Element rootElement) {
        this(rootElement, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     */
    public DomWriter(Document document, XmlFriendlyReplacer replacer) {
        super(replacer);
        this.document = document;
        this.current = document.getDocumentElement();
    }

    /**
     * @since 1.2
     */
    public DomWriter(Element rootElement, XmlFriendlyReplacer replacer) {
        super(replacer);
        document = rootElement.getOwnerDocument();
        current = rootElement;
    }

    public void startNode(String name) {
        final Element child = document.createElement(escapeXmlName(name));
        if (current == null) {
            document.appendChild(child);
        } else {
            current.appendChild(child);
        }
        current = child;
    }

    public void startNode(String name, Class clazz) {
        startNode(name);
    }

    public void addAttribute(String name, String value) {
        current.setAttribute(escapeXmlName(name), value);
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