package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.alias.ClassMapper;

public class MarshallingContextAdaptor implements MarshallingContext {

    private HierarchicalStreamWriter writer;
    private ConverterLookup converterLookup;
    private ClassMapper classMapper;

    public MarshallingContextAdaptor(HierarchicalStreamWriter writer, ConverterLookup converterLookup,
                                     ClassMapper classMapper) {
        this.writer = writer;
        this.converterLookup = converterLookup;
        this.classMapper = classMapper;
    }

    public void convertAnother(Object item) {
        Converter converter = converterLookup.lookupConverterForType(item.getClass());
        converter.marshal(item, writer, this);
    }

    public void start(Object item) {
        Converter rootConverter = converterLookup.lookupConverterForType(item.getClass());
        writer.startNode(classMapper.lookupName(item.getClass()));
        rootConverter.marshal(item, writer, this);
        writer.endNode();
    }

}
