package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ReferenceByIdMarshallingStrategy implements MarshallingStrategy {

    public Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, DefaultConverterLookup converterLookup, ClassMapper classMapper) {
        return new ReferenceByIdUnmarshaller(
                root, reader, converterLookup,
                classMapper).start(dataHolder);
    }

    public void marshal(HierarchicalStreamWriter writer, Object obj, DefaultConverterLookup converterLookup, ClassMapper classMapper, DataHolder dataHolder) {
        new ReferenceByIdMarshaller(
                writer, converterLookup, classMapper).start(obj, dataHolder);
    }

}
