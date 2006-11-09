package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;


public class XppDomWriter extends AbstractDocumentWriter {
    public XppDomWriter() {
        this(null, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2.1
     */
    public XppDomWriter(final Xpp3Dom parent) {
        this(parent, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     */
    public XppDomWriter(final XmlFriendlyReplacer replacer) {
        this(null, replacer);
    }

    /**
     * @since 1.2.1
     */
    public XppDomWriter(final Xpp3Dom parent, final XmlFriendlyReplacer replacer) {
        super(parent, replacer);
    }

    public Xpp3Dom getConfiguration() {
        return (Xpp3Dom)getTopLevelNodes().get(0);
    }

    protected Object createNode(final String name) {
        final Xpp3Dom newNode = new Xpp3Dom(escapeXmlName(name));
        final Xpp3Dom top = top();
        if (top != null) {
            top().addChild(newNode);
        }
        return newNode;
    }

    public void setValue(final String text) {
        top().setValue(text);
    }

    public void addAttribute(final String key, final String value) {
        top().setAttribute(escapeXmlName(key), value);
    }

    private Xpp3Dom top() {
        return (Xpp3Dom)getCurrent();
    }
}
