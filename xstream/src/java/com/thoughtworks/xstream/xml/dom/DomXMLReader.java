package com.thoughtworks.xstream.xml.dom;

import com.thoughtworks.xstream.xml.XMLReader;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;

public class DomXMLReader implements XMLReader {

    private Element currentElement;
    private List childElements;
    private StringBuffer textBuffer;

    public DomXMLReader(Element rootElement) {
        setCurrent(rootElement);
    }

    public DomXMLReader(Document document) {
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

    public int childCount() {
        return childElements.size();
    }

    public void child(int index) {
        setCurrent(childElements.get(index));
    }

    public void pop() {
        setCurrent(currentElement.getParentNode());
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

}
