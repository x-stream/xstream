package com.thoughtworks.xstream.io.xml;

import org.dom4j.Branch;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;


public class Dom4JWriter extends AbstractDocumentWriter {

    private final DocumentFactory documentFactory;

    /**
     * @since 1.2.1
     */
    public Dom4JWriter(
                       final Branch root, final DocumentFactory factory,
                       final XmlFriendlyReplacer replacer) {
        super(root, replacer);
        documentFactory = factory;
    }

    /**
     * @since 1.2.1
     */
    public Dom4JWriter(final DocumentFactory factory, final XmlFriendlyReplacer replacer) {
        this(null, factory, replacer);
    }

    /**
     * @since 1.2.1
     */
    public Dom4JWriter(final DocumentFactory documentFactory) {
        this(documentFactory, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2.1
     */
    public Dom4JWriter(final Branch root, final XmlFriendlyReplacer replacer) {
        this(root, new DocumentFactory(), replacer);
    }

    public Dom4JWriter(final Branch root) {
        this(root, new DocumentFactory(), new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2.1
     */
    public Dom4JWriter() {
        this(new DocumentFactory(), new XmlFriendlyReplacer());
    }

    protected Object createNode(final String name) {
        final Element element = documentFactory.createElement(escapeXmlName(name));
        final Branch top = top();
        if (top != null) {
            top().add(element);
        }
        return element;
    }

    public void setValue(final String text) {
        top().setText(text);
    }

    public void addAttribute(final String key, final String value) {
        ((Element)top()).addAttribute(escapeXmlName(key), value);
    }

    private Branch top() {
        return (Branch)getCurrent();
    }
}
