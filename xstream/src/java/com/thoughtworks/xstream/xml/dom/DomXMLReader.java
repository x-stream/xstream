package com.thoughtworks.xstream.xml.dom;

import com.thoughtworks.xstream.xml.XMLReader;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DomXMLReader implements XMLReader {

    private Element currentElement;
    private List childElements;
    private StringBuffer textBuffer;
    private LinkedList pointers = new LinkedList();

    public DomXMLReader(Element rootElement) {
        pointers.addLast(new Pointer());
        setCurrent(rootElement);
    }

    public DomXMLReader(Document document) {
        pointers.addLast(new Pointer());
        setCurrent(document.getDocumentElement());
    }

    public String name() {
        return currentElement.getTagName();
    }

    public String text() {
        return textBuffer.toString();
    }

    public String attribute(String name) {
        Attr attribute = currentElement.getAttributeNode(name);
        return attribute == null ? null : attribute.getValue();
    }

    public void pop() {
        setCurrent(currentElement.getParentNode());
        pointers.removeLast();
    }

    private void setCurrent(Object currentElementObj) {
        this.currentElement = (Element) currentElementObj;
        childElements = new ArrayList();
        textBuffer = new StringBuffer();
        NodeList childNodes = currentElement.getChildNodes();
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node childNode = childNodes.item(i);
            if (childNode instanceof Element) {
                Element element = (Element) childNode;
                childElements.add(element);
            } else if (childNode instanceof Text) {
                Text text = (Text) childNode;
                textBuffer.append(text.getData());
            }
        }
    }

    public boolean nextChild() {
        Pointer pointer = (Pointer) pointers.getLast();
        if (pointer.v < childElements.size()) {
            pointers.addLast(new Pointer());
            setCurrent(childElements.get(pointer.v));
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
