package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class MarshallingContextAdaptor implements MarshallingContext {

    private HierarchicalStreamWriter writer;
    private ConverterLookup converterLookup;

    public MarshallingContextAdaptor(HierarchicalStreamWriter xmlWriter, ConverterLookup converterLookup) {
        this.writer = xmlWriter;
        this.converterLookup = converterLookup;
    }

    public void convertAnother(Object item) {
        Converter converter = converterLookup.lookupConverterForType(item.getClass());
        converter.toXML(item, writer, this);
    }

}
