package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import nu.xom.Attribute;
import nu.xom.Element;

public class XomWriter extends AbstractXmlWriter {

    private Element node;

    public XomWriter(Element parentElement) {
        this(parentElement, new XmlFriendlyReplacer());
    }
    
    /**
     * @since 1.2
     */
    public XomWriter(Element parentElement, XmlFriendlyReplacer replacer) {
        super(replacer);
        this.node = parentElement;
    }

    public void startNode(String name) {
        Element newNode = new Element(escapeXmlName(name));
        node.appendChild(newNode);
        node = newNode;
    }

    public void startNode(String name, Class clazz) {
        startNode(name);
    }

    public void addAttribute(String name, String value) {
        node.addAttribute(new Attribute(escapeXmlName(name), value));
    }

    public void setValue(String text) {
        node.appendChild(text);
    }

    public void endNode() {
        node = (Element) node.getParent();
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
