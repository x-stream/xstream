package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ReferenceByXPathMarshallingStrategy implements MarshallingStrategy {

    public Object unmarshal(Object root, HierarchicalStreamReader reader, DefaultConverterLookup converterLookup, ClassMapper classMapper) {
        return new ReferenceByXPathUnmarshaller(root, reader, converterLookup,
                classMapper, converterLookup.getClassAttributeIdentifier()).start();
    }

    public void marshal(HierarchicalStreamWriter writer, Object obj, DefaultConverterLookup converterLookup, ClassMapper classMapper) {
        new ReferenceByXPathMarshaller(writer, converterLookup, classMapper).start(obj);
    }
}
