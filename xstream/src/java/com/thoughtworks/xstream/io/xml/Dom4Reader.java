package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.LinkedList;

public class Dom4Reader implements HierarchicalStreamReader {

    private Element currentElement;
    private LinkedList pointers = new LinkedList();

    public Dom4Reader(Element rootElement) {
        currentElement = rootElement;
        pointers.addLast(new Pointer());
    }

    public Dom4Reader(Document document) {
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

    public void getParentNode() {
        currentElement = currentElement.getParent();
        pointers.removeLast();
    }

    public Object peekUnderlyingNode() {
        return currentElement;
    }
    
    public boolean getNextChildNode() {
        Pointer pointer = (Pointer) pointers.getLast();
        if (pointer.v < currentElement.elements().size()) {
            pointers.addLast(new Pointer());
            currentElement = (Element) currentElement.elements().get(pointer.v);
            pointer.v++;
            return true;
        } else {
            return false;
        }
    }

    private class Pointer {
        public int v;
    }

}
