package com.thoughtworks.xstream.io.xml;

import java.util.List;
import java.util.LinkedList;

import org.jdom.Element;
import org.jdom.JDOMFactory;
import org.jdom.DefaultJDOMFactory;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author Laurent Bihanic
 */
public class JDomWriter implements HierarchicalStreamWriter {

    private List result = new LinkedList();
    private List elementStack = new LinkedList();
    private final JDOMFactory documentFactory;

    public JDomWriter(Element container, JDOMFactory factory) {
        elementStack.add(0, container);
        this.result.add(container);
        this.documentFactory = factory;
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
        Element element = this.documentFactory.element(name);

        Element parent = this.top();
        if (parent != null) {
            parent.addContent(element);
        }
        else {
            this.result.add(element);
        }
        elementStack.add(0, element);
    }

    public void setValue(String text) {
        this.top().addContent(this.documentFactory.text(text));
    }

    public void addAttribute(String key, String value) {
        ((Element) this.top()).setAttribute(
                        this.documentFactory.attribute(key, value));
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
