package com.thoughtworks.xstream.converters.old;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.xml.XMLReader;

import java.util.LinkedList;

public class UnmarshallingContextAdaptor implements UnmarshallingContext {

    private Object root;
    private XMLReader xmlReader;
    private ConverterLookup converterLookup;
    private LinkedList types = new LinkedList();

    public UnmarshallingContextAdaptor(Object root, XMLReader xmlReader, ConverterLookup converterLookup) {
        this.root = root;
        this.xmlReader = xmlReader;
        this.converterLookup = converterLookup;
    }

    public String xmlText() {
        return xmlReader.text();
    }

    public String xmlElementName() {
        return xmlReader.name();
    }

    public void xmlPop() {
        xmlReader.pop();
    }

    public boolean xmlNextChild() {
        return xmlReader.nextChild();
    }

    public String xmlAttribute(String name) {
        return xmlReader.attribute(name);
    }

    public Object xmlPeek() {
        return xmlReader.peek();
    }

    public Object convertAnother(Class type) {
        Converter converter = converterLookup.lookupConverterForType(type);
        types.addLast(type);
        Object result = converter.fromXML(this);
        types.removeLast();
        return result;
    }

    public Object currentObject() {
        return root;
    }

    public Class getRequiredType() {
        return (Class) types.getLast();
    }

}

