package com.thoughtworks.xstream.xml.dom4j;

import com.thoughtworks.xstream.xml.XMLReader;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.LinkedList;

public class Dom4JXMLReader implements XMLReader {

    private Element currentElement;
    private LinkedList pointers = new LinkedList();

    public Dom4JXMLReader(Element rootElement) {
        currentElement = rootElement;
        pointers.addLast(new Pointer());
    }

    public Dom4JXMLReader(Document document) {
        currentElement = document.getRootElement();
        pointers.addLast(new Pointer());
    }

    public String name() {
        return currentElement.getName();
    }

    public String text() {
        return currentElement.getText();
    }

    public String attribute(String name) {
        return currentElement.attributeValue(name);
    }

    public void pop() {
        currentElement = currentElement.getParent();
        pointers.removeLast();
    }

    public Object peek() {
        return currentElement;
    }
    
    public boolean nextChild() {
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
