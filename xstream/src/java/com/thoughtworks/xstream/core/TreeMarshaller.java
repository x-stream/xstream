package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.XStream;

public class TreeMarshaller implements Marshaller, MarshallingContext {

    protected HierarchicalStreamWriter writer;
    protected ConverterLookup converterLookup;
    protected ClassMapper classMapper;

    public TreeMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup,
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
        writer.startNode(classMapper.lookupName(item.getClass()));
        convertAnother(item);
        writer.endNode();
    }

}
