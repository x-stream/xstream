package com.thoughtworks.xstream.xml.dom;

import com.thoughtworks.xstream.xml.XMLReader;
import org.w3c.dom.*;

import java.util.LinkedList;

public class DomXMLReader implements XMLReader {

    private Element currentElement;
    private StringBuffer textBuffer;
    private NodeList childNodes;
    private LinkedList pointers = new LinkedList();

    public DomXMLReader(Element rootElement) {
    	textBuffer = new StringBuffer(180);
        pointers.addLast(new Pointer());
        setCurrent(rootElement);
    }

    public DomXMLReader(Document document) {
		textBuffer = new StringBuffer(180);
        pointers.addLast(new Pointer());
        setCurrent(document.getDocumentElement());
    }

    public String name() {
        return currentElement.getTagName();
    }

    public String text() {
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

    public String attribute(String name) {
        Attr attribute = currentElement.getAttributeNode(name);
        return attribute == null ? null : attribute.getValue();
    }

    public void pop() {
        setCurrent(currentElement.getParentNode());
        pointers.removeLast();
    }

    public Object peek() {
        return currentElement;
    }

    private void setCurrent(Object currentElementObj) {
        this.currentElement = (Element) currentElementObj;
        childNodes = currentElement.getChildNodes();
    }

    public boolean nextChild() {
        Pointer pointer = (Pointer) pointers.getLast();
        int len = childNodes.getLength();
        for(; pointer.v < len; pointer.v++){
        	if (childNodes.item(pointer.v) instanceof Element){
        		pointers.addLast(new Pointer());
        		setCurrent((Element) childNodes.item(pointer.v));
        		pointer.v++;
        		return true;
        	}
        }
        return false;
    }

    private class Pointer {
        public int v;
    }

}
