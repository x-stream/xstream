package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;
import org.jdom.JDOMFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Laurent Bihanic
 */
public class JDomWriter extends AbstractXmlWriter {

    private List result = new LinkedList();
    private List elementStack = new LinkedList();
    private final JDOMFactory documentFactory;

    /**
     * @since 1.2
     */
    public JDomWriter(Element container, JDOMFactory factory, XmlFriendlyReplacer replacer) {
        super(replacer);
        elementStack.add(0, container);
        result.add(container);
        this.documentFactory = factory;
    }

    public JDomWriter(Element container, JDOMFactory factory) {
        this(container, factory, new XmlFriendlyReplacer());
    }

    public JDomWriter(JDOMFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    public JDomWriter(Element container) {
        this(container, new DefaultJDOMFactory());
    }

    public JDomWriter() {
        this(new DefaultJDOMFactory());
    }

    public void startNode(String name) {
        Element element = this.documentFactory.element(escapeXmlName(name));

        Element parent = this.top();
        if (parent != null) {
            parent.addContent(element);
        }
        else {
            result.add(element);
        }
        elementStack.add(0, element);
    }

    public void startNode(String name, Class clazz) {
        startNode(name);
    }

    public void setValue(String text) {
        top().addContent(this.documentFactory.text(text));
    }

    public void addAttribute(String key, String value) {
        top().setAttribute(
                        this.documentFactory.attribute(escapeXmlName(key), value));
    }

    public void endNode() {
        this.elementStack.remove(0);
    }

    private Element top() {
        Element top = null;

        if (this.elementStack.isEmpty() == false) {
            top = (Element) this.elementStack.get(0);
        }
        return top;
    }

    public List getResult() {
        return this.result;
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
