package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.converters.ErrorWriter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Parent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Laurent Bihanic
 */
public class JDomReader implements HierarchicalStreamReader {

    private Element currentElement;
    private List pointers = new LinkedList();

    public JDomReader(Document document) {
        this(document.getRootElement());
    }

    public JDomReader(Element rootElement) {
        this.currentElement = rootElement;
        this.pointers.add(0, new Pointer(this.currentElement));
    }

    public String getNodeName() {
        return this.currentElement.getName();
    }

    public String getValue() {
        return this.currentElement.getText();
    }

    public String getAttribute(String name) {
        return this.currentElement.getAttributeValue(name);
    }

    public Object peekUnderlyingNode() {
        return this.currentElement;
    }

    public void appendErrors(ErrorWriter errorWriter) {

    }

    public boolean hasMoreChildren() {
        return ((Pointer) this.pointers.get(0)).hasNext();
    }

    public void moveUp() {
        // JDOM b9 and earlier:
//        this.currentElement = this.currentElement.getParent();

        // JDOM b10 and later:
        Parent parent = this.currentElement.getParent();
        this.currentElement = (parent instanceof Element)? (Element)parent: null;

        this.pointers.remove(0);
    }

    public void moveDown() {
        Pointer pointer = (Pointer) this.pointers.get(0);

        this.currentElement = pointer.next();
        this.pointers.add(0, new Pointer(this.currentElement));
    }

    private static class Pointer {
        private final Iterator children;

        public Pointer(Element e) {
            this.children = e.getChildren().iterator();
        }

        public boolean hasNext() {
            return this.children.hasNext();
        }

        public Element next() {
            return (Element) this.children.next();
        }
    }
}

