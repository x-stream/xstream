package com.thoughtworks.xstream.xml.dom4j;

import com.thoughtworks.xstream.xml.XMLReader;
import org.dom4j.Document;
import org.dom4j.Element;

public class Dom4JXMLReader implements XMLReader {

    private Element currentElement;

    public Dom4JXMLReader(Element rootElement) {
        currentElement = rootElement;
    }

    public Dom4JXMLReader(Document document) {
        currentElement = document.getRootElement();
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

    public int childCount() {
        return currentElement.elements().size();
    }

    public void child(int index) {
        currentElement = (Element) currentElement.elements().get(index);
    }

    public void child(String elementName) {
        currentElement = currentElement.element(elementName);
    }

    public void pop() {
        currentElement = currentElement.getParent();
    }

    public boolean childExists(String elementName) {
        return currentElement.element(elementName) != null;
    }
}
