package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.core.util.ObjectIdDictionary;

import java.util.Collection;
import java.util.HashSet;

public class TreeMarshaller implements MarshallingContext {

    protected HierarchicalStreamWriter writer;
    protected ConverterLookup converterLookup;
    protected ClassMapper classMapper;
    private ObjectIdDictionary parentObjects = new ObjectIdDictionary();

    public TreeMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup,
                                     ClassMapper classMapper) {
        this.writer = writer;
        this.converterLookup = converterLookup;
        this.classMapper = classMapper;
    }

    public void convertAnother(Object item) {
        if (parentObjects.containsId(item)) {
            throw new CircularReferenceException();
        }
        parentObjects.associateId(item, "");
        Converter converter = converterLookup.lookupConverterForType(item.getClass());
        converter.marshal(item, writer, this);
        parentObjects.removeId(item);
    }

    public void start(Object item) {
        if (item == null) {
            writer.startNode(classMapper.lookupName(ClassMapper.Null.class));
            writer.endNode();
        } else {
            writer.startNode(classMapper.lookupName(item.getClass()));
            convertAnother(item);
            writer.endNode();
        }
    }

    public static class CircularReferenceException extends RuntimeException {
    }

}
