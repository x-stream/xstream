package com.thoughtworks.xstream.xml.dom4j;

import com.thoughtworks.xstream.xml.XMLWriter;
import org.dom4j.Branch;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import java.util.LinkedList;

public class Dom4JXMLWriter implements XMLWriter {

    private DocumentFactory documentFactory = new DocumentFactory();
    private LinkedList elementStack = new LinkedList();

    public Dom4JXMLWriter(Branch container) {
        elementStack.addLast(container);
    }

    public void pushElement(String name) {
        Element element = documentFactory.createElement(name);
        top().add(element);
        elementStack.addLast(element);
    }

    public void text(String text) {
        top().setText(text);
    }

    public void attribute(String key, String value) {
        ((Element) top()).addAttribute(key, value);
    }

    public void pop() {
        elementStack.removeLast();
    }

    private Branch top() {
        return (Branch) elementStack.getLast();
    }

}
