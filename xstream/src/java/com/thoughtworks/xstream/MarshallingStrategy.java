package com.thoughtworks.xstream;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.alias.ClassMapper;

public interface MarshallingStrategy {

    Object unmarshal(Object root, HierarchicalStreamReader reader, DefaultConverterLookup converterLookup, ClassMapper classMapper);

    void marshal(HierarchicalStreamWriter writer, Object obj, DefaultConverterLookup converterLookup, ClassMapper classMapper);

}
