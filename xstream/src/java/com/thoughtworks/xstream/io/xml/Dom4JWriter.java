package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.dom4j.Branch;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import java.util.LinkedList;

public class Dom4JWriter implements HierarchicalStreamWriter {

    private DocumentFactory documentFactory = new DocumentFactory();
    private LinkedList elementStack = new LinkedList();

    public Dom4JWriter(Branch container) {
        elementStack.addLast(container);
    }

    public void startElement(String name) {
        Element element = documentFactory.createElement(name);
        top().add(element);
        elementStack.addLast(element);
    }

    public void writeText(String text) {
        top().setText(text);
    }

    public void addAttribute(String key, String value) {
        ((Element) top()).addAttribute(key, value);
    }

    public void endElement() {
        elementStack.removeLast();
    }

    private Branch top() {
        return (Branch) elementStack.getLast();
    }

}
