package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.ClassStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class UnmarshallingContextAdaptor implements UnmarshallingContext {

    private Object root;
    private HierarchicalStreamReader reader;
    private ConverterLookup converterLookup;
    private ClassStack types = new ClassStack(16);

    public UnmarshallingContextAdaptor(Object root, HierarchicalStreamReader xmlReader, ConverterLookup converterLookup) {
        this.root = root;
        this.reader = xmlReader;
        this.converterLookup = converterLookup;
    }

    public Object convertAnother(Class type) {
        Converter converter = converterLookup.lookupConverterForType(type);
        types.push(type);
        Object result = converter.unmarshal(reader, this);
        types.popSilently();
        return result;
    }

    public Object currentObject() {
        return root;
    }

    public Class getRequiredType() {
        return types.peek();
    }

}

