package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.core.util.ObjectIdDictionary;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Iterator;

public class TreeMarshaller implements MarshallingContext {

    protected HierarchicalStreamWriter writer;
    protected ConverterLookup converterLookup;
    /**
     * @deprecated As of 1.2, use {@link #mapper}
     */
    protected ClassMapper classMapper;
    private Mapper mapper;
    private ObjectIdDictionary parentObjects = new ObjectIdDictionary();
    private DataHolder dataHolder;

    public TreeMarshaller(HierarchicalStreamWriter writer,
                          ConverterLookup converterLookup,
                          Mapper mapper) {
        this.writer = writer;
        this.converterLookup = converterLookup;
        this.mapper = mapper;
        // TODO: Remove when deprecated ClassMapper goes away
        if (mapper instanceof ClassMapper) {
            classMapper = (ClassMapper)mapper;
        }
    }

    /**
     * @deprecated As of 1.2, use {@link #TreeMarshaller(HierarchicalStreamWriter, ConverterLookup, Mapper)}
     */
    public TreeMarshaller(HierarchicalStreamWriter writer,
                          ConverterLookup converterLookup,
                          ClassMapper classMapper) {
        this(writer, converterLookup, (Mapper)classMapper);
    }

    public void convertAnother(Object item) {
        Converter converter = converterLookup.lookupConverterForType(item.getClass());
        convert(item, converter);
    }

    public void convertAnother(Object item, Converter converter) {
    	convert(item, converter);
    }
    
    protected void convert(Object item, Converter converter) {
        if (parentObjects.containsId(item)) {
            throw new CircularReferenceException();
        }
        parentObjects.associateId(item, "");
        converter.marshal(item, writer, this);
        parentObjects.removeId(item);
    }

    public void start(Object item, DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        if (item == null) {
            writer.startNode(mapper.serializedClass(null));
            writer.endNode();
        } else {
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper.serializedClass(item.getClass()), item.getClass());
            convertAnother(item);
            writer.endNode();
        }
    }

    public Object get(Object key) {
        lazilyCreateDataHolder();
        return dataHolder.get(key);
    }

    public void put(Object key, Object value) {
        lazilyCreateDataHolder();
        dataHolder.put(key, value);
    }

    public Iterator keys() {
        lazilyCreateDataHolder();
        return dataHolder.keys();
    }

    private void lazilyCreateDataHolder() {
        if (dataHolder == null) {
            dataHolder = new MapBackedDataHolder();
        }
    }

    protected Mapper getMapper() {
        return this.mapper;
    }

    public static class CircularReferenceException extends RuntimeException {
    }
}
