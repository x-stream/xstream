package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.LinkedList;

public class Dom4JReader implements HierarchicalStreamReader {

    private Element currentElement;
    private LinkedList pointers = new LinkedList();

    public Dom4JReader(Element rootElement) {
        currentElement = rootElement;
        pointers.addLast(new Pointer());
    }

    public Dom4JReader(Document document) {
        currentElement = document.getRootElement();
        pointers.addLast(new Pointer());
    }

    public String getNodeName() {
        return currentElement.getName();
    }

    public String getValue() {
        return currentElement.getText();
    }

    public String getAttribute(String name) {
        return currentElement.attributeValue(name);
    }

    public Object peekUnderlyingNode() {
        return currentElement;
    }

    private class Pointer {
        public int v;
    }

    public boolean hasMoreChildren() {
        Pointer pointer = (Pointer) pointers.getLast();

        if (pointer.v < currentElement.elements().size()) {
            return true;
        } else {
            return false;
        }
    }

    public void moveUp() {
        currentElement = currentElement.getParent();
        pointers.removeLast();
    }

    public void moveDown() {
        Pointer pointer = (Pointer) pointers.getLast();
        pointers.addLast(new Pointer());

        currentElement = (Element) currentElement.elements().get(pointer.v);

        pointer.v++;

    }
}
