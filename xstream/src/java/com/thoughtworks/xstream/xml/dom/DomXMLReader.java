package com.thoughtworks.xstream.xml.dom;

import com.thoughtworks.xstream.xml.XMLReader;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomXMLReader implements XMLReader {

    private Element currentElement;
    private List childElementsByIndex;
    private Map childElementsByName;
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
        //return currentElement.getText();
        return textBuffer.toString();
    }

    public String attribute(String name) {
        Attr attribute = currentElement.getAttributeNode(name);
        return attribute == null ? null : attribute.getValue();
    }

    public int childCount() {
        return childElementsByIndex.size();
    }

    public void child(int index) {
        setCurrent(childElementsByIndex.get(index));
    }

    public void child(String elementName) {
        setCurrent(childElementsByName.get(elementName));
    }

    public void pop() {
        setCurrent(currentElement.getParentNode());
    }

    private void setCurrent(Object currentElementObj) {
        this.currentElement = (Element) currentElementObj;
        childElementsByIndex = new ArrayList();
        childElementsByName = new HashMap();
        textBuffer = new StringBuffer();
        NodeList childNodes = currentElement.getChildNodes();
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node childNode = childNodes.item(i);
            if (childNode instanceof Element) {
                Element element = (Element) childNode;
                childElementsByIndex.add(element);
                childElementsByName.put(element.getTagName(), element);
            } else if (childNode instanceof Text) {
                Text text = (Text) childNode;
                textBuffer.append(text.getData());
            }
        }
    }

    public boolean childExists(String elementName) {
        return childElementsByName.containsKey(elementName);
    }

}
