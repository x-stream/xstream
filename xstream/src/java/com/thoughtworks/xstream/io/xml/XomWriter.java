package com.thoughtworks.xstream.io.xml;

import nu.xom.Attribute;
import nu.xom.Element;


public class XomWriter extends AbstractDocumentWriter {

    /**
     * @since 1.2.1
     */
    public XomWriter() {
        this(null);
    }

    public XomWriter(final Element parentElement) {
        this(parentElement, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     */
    public XomWriter(final Element parentElement, final XmlFriendlyReplacer replacer) {
        super(parentElement, replacer);
    }

    protected Object createNode(final String name) {
        final Element newNode = new Element(escapeXmlName(name));
        final Element top = top();
        if (top != null){
            top().appendChild(newNode);
        }
        return newNode;
    }

    public void addAttribute(final String name, final String value) {
        top().addAttribute(new Attribute(escapeXmlName(name), value));
    }

    public void setValue(final String text) {
        top().appendChild(text);
    }

    private Element top() {
        return (Element)getCurrent();
    }
}
