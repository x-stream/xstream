package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import java.io.IOException;

public class Dom4JWriter implements HierarchicalStreamWriter {

    private final DocumentFactory documentFactory;
    private final FastStack elementStack = new FastStack(16);
    private final XMLWriter writer;

    public Dom4JWriter(DocumentFactory documentFactory, XMLWriter writer) {
        this.documentFactory = documentFactory;
        this.writer = writer;
        elementStack.push(documentFactory.createDocument());
    }

    public void startNode(String name) {
        Element element = documentFactory.createElement(name);
        top().add(element);
        elementStack.push(element);
    }

    public void setValue(String text) {
        top().setText(text);
    }

    public void addAttribute(String key, String value) {
        ((Element) top()).addAttribute(key, value);
    }

    public void endNode() {
        elementStack.popSilently();
    }

    private Branch top() {
        return (Branch) elementStack.peek();
    }

    public void flush() {
        if (elementStack.size() == 1) {
            final Document document = (Document)elementStack.peek();
            try {
                writer.write(document);
                writer.flush();
            } catch (IOException e) {
                throw new StreamException(e);
            }
        }
    }

    public void close() {
        flush();
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }
}
