package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.w3c.dom.*;

import java.util.LinkedList;

public class DomReader implements HierarchicalStreamReader {

    private Element currentElement;
    private StringBuffer textBuffer;
    private NodeList childNodes;
    private LinkedList pointers = new LinkedList();

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

    public void getParentNode() {
        setCurrent(currentElement.getParentNode());
        pointers.removeLast();
    }

    public Object peekUnderlyingNode() {
        return currentElement;
    }

    private void setCurrent(Object currentElementObj) {
        this.currentElement = (Element) currentElementObj;
        childNodes = currentElement.getChildNodes();
    }

    public boolean getNextChildNode() {
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
