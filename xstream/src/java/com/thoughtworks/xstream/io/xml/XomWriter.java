package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import nu.xom.Element;
import nu.xom.Attribute;

public class XomWriter implements HierarchicalStreamWriter {

    private Element node;

    public XomWriter(Element parentElement) {
        this.node = parentElement;
    }

    public void startNode(String name) {
        Element newNode = new Element(name);
        node.appendChild(newNode);
        node = newNode;
    }

    public void addAttribute(String name, String value) {
        node.addAttribute(new Attribute(name, value));
    }

    public void setValue(String text) {
        node.appendChild(text);
    }

    public void endNode() {
        node = (Element) node.getParent();
    }
}
