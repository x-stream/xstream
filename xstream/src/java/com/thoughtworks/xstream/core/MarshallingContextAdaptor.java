package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.LinkedList;

public class MarshallingContextAdaptor implements MarshallingContext {

    private HierarchicalStreamWriter writer;
    private ConverterLookup converterLookup;

    private LinkedList stack = new LinkedList();

    public MarshallingContextAdaptor(Object root, HierarchicalStreamWriter xmlWriter, ConverterLookup converterLookup) {
        stack.add(root);
        this.writer = xmlWriter;
        this.converterLookup = converterLookup;
    }

    public void convertAnother(Object item) {
        stack.addLast(item);
        Converter converter = converterLookup.lookupConverterForType(item.getClass());
        converter.toXML(stack.getLast(), writer, this);
        stack.removeLast();
    }

}
