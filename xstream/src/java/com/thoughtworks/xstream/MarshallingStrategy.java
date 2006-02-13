package com.thoughtworks.xstream;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public interface MarshallingStrategy {

    Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, ConverterLookup converterLookup, Mapper mapper);
    void marshal(HierarchicalStreamWriter writer, Object obj, ConverterLookup converterLookup, Mapper mapper, DataHolder dataHolder);
    
    /**
     * @deprecated As of 1.2, use {@link #unmarshal(Object, HierarchicalStreamReader, DataHolder, DefaultConverterLookup, Mapper)}
     */
    Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, DefaultConverterLookup converterLookup, ClassMapper classMapper);

    /**
     * @deprecated As of 1.2, use {@link #marshal(HierarchicalStreamWriter, Object, ConverterLookup, Mapper, DataHolder)}
     */
    void marshal(HierarchicalStreamWriter writer, Object obj, DefaultConverterLookup converterLookup, ClassMapper classMapper, DataHolder dataHolder);

}
