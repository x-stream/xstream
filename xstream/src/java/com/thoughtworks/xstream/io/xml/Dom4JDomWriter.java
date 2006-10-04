package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import org.dom4j.Branch;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

public class Dom4JDomWriter extends AbstractXmlWriter {

    private final DocumentFactory documentFactory;
    private final FastStack elementStack = new FastStack(16);

    public Dom4JDomWriter(final DocumentFactory documentFactory, final Branch root, XmlFriendlyReplacer replacer) {
        super(replacer);
        this.documentFactory = documentFactory;
        elementStack.push(root);
    }

    public Dom4JDomWriter(final DocumentFactory documentFactory, XmlFriendlyReplacer replacer) {
        this(documentFactory, documentFactory.createDocument(), replacer);
    }

    public Dom4JDomWriter(final DocumentFactory documentFactory) {
        this(documentFactory, documentFactory.createDocument(), new XmlFriendlyReplacer());
    }

    public void startNode(String name) {
        Element element = documentFactory.createElement(escapeXmlName(name));
        top().add(element);
        elementStack.push(element);
    }

    public void setValue(String text) {
        top().setText(text);
    }

    public void addAttribute(String key, String value) {
        ((Element) top()).addAttribute(escapeXmlName(key), value);
    }

    public void endNode() {
        elementStack.popSilently();
    }

    private Branch top() {
        return (Branch) elementStack.peek();
    }

    public void flush() {
    }

    public void close() {
    }
    
    public Branch root() {
        return (Branch)elementStack.get(0);
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }
}
