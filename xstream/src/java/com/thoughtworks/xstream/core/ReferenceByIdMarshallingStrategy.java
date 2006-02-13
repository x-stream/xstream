package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class ReferenceByIdMarshallingStrategy implements MarshallingStrategy {

    public Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, ConverterLookup converterLookup, Mapper mapper) {
        return new ReferenceByIdUnmarshaller(root, reader, converterLookup, mapper)
            .start(dataHolder);
    }

    public void marshal(HierarchicalStreamWriter writer, Object obj, ConverterLookup converterLookup, Mapper mapper, DataHolder dataHolder) {
        new ReferenceByIdMarshaller(writer, converterLookup, mapper)
            .start(obj, dataHolder);
    }

    /**
     * @deprecated As of 1.2, use {@link #unmarshal(Object, HierarchicalStreamReader, DataHolder, DefaultConverterLookup, Mapper)}
     */
    public Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, DefaultConverterLookup converterLookup, ClassMapper classMapper) {
        return unmarshal(root, reader, dataHolder, (ConverterLookup)converterLookup, (Mapper)classMapper);
    }

    /**
     * @deprecated As of 1.2, use {@link #marshal(HierarchicalStreamWriter, Object, ConverterLookup, Mapper, DataHolder)}
     */
    public void marshal(HierarchicalStreamWriter writer, Object obj, DefaultConverterLookup converterLookup, ClassMapper classMapper, DataHolder dataHolder) {
        marshal(writer, obj, converterLookup, (Mapper)classMapper, dataHolder);
    }
}
