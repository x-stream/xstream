package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.core.*;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.MarshallingStrategy;

public class TreeMarshallingStrategy implements MarshallingStrategy {

    public Object unmarshal(Object root, HierarchicalStreamReader reader, DefaultConverterLookup converterLookup, ClassMapper classMapper) {
        return new TreeUnmarshaller(
                root, reader, converterLookup,
                classMapper, converterLookup.getClassAttributeIdentifier()).start();
    }

    public void marshal(HierarchicalStreamWriter writer, Object obj, DefaultConverterLookup converterLookup, ClassMapper classMapper) {
        new TreeMarshaller(
                writer, converterLookup, classMapper).start(obj);
    }

}
