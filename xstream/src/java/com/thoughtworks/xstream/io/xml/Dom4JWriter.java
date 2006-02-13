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

    public void startNode(String name) {
        Element element = documentFactory.createElement(name);
        top().add(element);
        elementStack.addLast(element);
    }

    public void setValue(String text) {
        top().setText(text);
    }

    public void addAttribute(String key, String value) {
        ((Element) top()).addAttribute(key, value);
    }

    public void endNode() {
        elementStack.removeLast();
    }

    private Branch top() {
        return (Branch) elementStack.getLast();
    }

    public void flush() {
        // don't need to do anything
    }

    public void close() {
        // don't need to do anything
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }
}
