package com.thoughtworks.xstream.io.xml;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;
import org.jdom.JDOMFactory;

import java.util.List;


/**
 * @author Laurent Bihanic
 */
public class JDomWriter extends AbstractDocumentWriter {

    private final JDOMFactory documentFactory;

    /**
     * @since 1.2
     */
    public JDomWriter(
                      final Element container, final JDOMFactory factory,
                      final XmlFriendlyReplacer replacer) {
        super(container, replacer);
        documentFactory = factory;
    }

    public JDomWriter(final Element container, final JDOMFactory factory) {
        this(container, factory, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2.1
     */
    public JDomWriter(final JDOMFactory factory, final XmlFriendlyReplacer replacer) {
        this(null, factory, replacer);
    }

    public JDomWriter(final JDOMFactory factory) {
        this(null, factory);
    }

    /**
     * @since 1.2.1
     */
    public JDomWriter(final Element container, final XmlFriendlyReplacer replacer) {
        this(container, new DefaultJDOMFactory(), replacer);
    }

    public JDomWriter(final Element container) {
        this(container, new DefaultJDOMFactory());
    }

    public JDomWriter() {
        this(new DefaultJDOMFactory());
    }

    protected Object createNode(final String name) {
        final Element element = documentFactory.element(escapeXmlName(name));
        final Element parent = top();
        if (parent != null) {
            parent.addContent(element);
        }
        return element;
    }

    public void setValue(final String text) {
        top().addContent(documentFactory.text(text));
    }

    public void addAttribute(final String key, final String value) {
        top().setAttribute(documentFactory.attribute(escapeXmlName(key), value));
    }

    private Element top() {
        return (Element)getCurrent();
    }

    /**
     * @deprecated since 1.2.1, use {@link #getTopLevelNodes()} instead
     */
    public List getResult() {
        return getTopLevelNodes();
    }
}
