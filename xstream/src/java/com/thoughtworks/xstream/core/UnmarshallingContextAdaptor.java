package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.util.LinkedList;

public class UnmarshallingContextAdaptor implements UnmarshallingContext {

    private Object root;
    private HierarchicalStreamReader reader;
    private ConverterLookup converterLookup;
    private LinkedList types = new LinkedList();

    public UnmarshallingContextAdaptor(Object root, HierarchicalStreamReader xmlReader, ConverterLookup converterLookup) {
        this.root = root;
        this.reader = xmlReader;
        this.converterLookup = converterLookup;
    }

    public Object convertAnother(Class type) {
        Converter converter = converterLookup.lookupConverterForType(type);
        types.addLast(type);
        Object result = converter.fromXML(reader, this);
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

