package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DomReader implements HierarchicalStreamReader {

    private Element currentElement;
    private StringBuffer textBuffer;
    private NodeList childNodes;
    private LinkedList pointers = new LinkedList();
    private List childElements;

    public DomReader(Element rootElement) {
    	textBuffer = new StringBuffer(180);
        pointers.addLast(new Pointer());
        setCurrent(rootElement);
    }

    public DomReader(Document document) {
		textBuffer = new StringBuffer(180);
        pointers.addLast(new Pointer());
        setCurrent(document.getDocumentElement());
    }

    public String getNodeName() {
        return currentElement.getTagName();
    }

    public String getValue() {
		NodeList childNodes = currentElement.getChildNodes();
		textBuffer.setLength(0);
		int length = childNodes.getLength();
		for (int i = 0; i < length; i++) {
			Node childNode = childNodes.item(i);
			if (childNode instanceof Text) {
				Text text = (Text) childNode;
				textBuffer.append(text.getData());
			}
		}
		return textBuffer.toString();
    }

    public String getAttribute(String name) {
        Attr attribute = currentElement.getAttributeNode(name);
        return attribute == null ? null : attribute.getValue();
    }

    public Object peekUnderlyingNode() {
        return currentElement;
    }

    private void setCurrent(Object currentElementObj) {
        this.currentElement = (Element) currentElementObj;
        childNodes = currentElement.getChildNodes();
        childElements = new ArrayList();
        for(int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                childElements.add(node);
            }
        }
    }

    private class Pointer {
        public int v;
    }

    public boolean hasMoreChildren() {
        Pointer pointer = (Pointer) pointers.getLast();

        if (pointer.v < childElements.size()) {
            return true;
        } else {
            return false;
        }
    }

    public void moveUp() {
        setCurrent(currentElement.getParentNode());
        pointers.removeLast();
    }

    public void moveDown() {
        Pointer pointer = (Pointer) pointers.getLast();
        pointers.addLast(new Pointer());

        setCurrent(childElements.get(pointer.v));

        pointer.v++;

    }


}
