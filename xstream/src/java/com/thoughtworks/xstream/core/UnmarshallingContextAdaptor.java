package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class UnmarshallingContextAdaptor implements UnmarshallingContext {

    private Object root;
    private HierarchicalStreamReader reader;
    private ConverterLookup converterLookup;
    private Class[] types = new Class[10];  // TODO: grow!
    private int pointer;

    public UnmarshallingContextAdaptor(Object root, HierarchicalStreamReader xmlReader, ConverterLookup converterLookup) {
        this.root = root;
        this.reader = xmlReader;
        this.converterLookup = converterLookup;
    }

    public Object convertAnother(Class type) {
        Converter converter = converterLookup.lookupConverterForType(type);
        types[++pointer] = type;
        Object result = converter.fromXML(reader, this);
        pointer--;
        return result;
    }

    public Object currentObject() {
        return root;
    }

    public Class getRequiredType() {
        return types[pointer];
    }

}

