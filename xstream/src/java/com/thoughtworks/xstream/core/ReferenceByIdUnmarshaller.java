package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.ClassStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.Map;
import java.util.HashMap;

public class ReferenceByIdUnmarshaller extends TreeUnmarshaller {

    private Map values = new HashMap();

    public ReferenceByIdUnmarshaller(Object root, HierarchicalStreamReader reader,
                                       ConverterLookup converterLookup, ClassMapper classMapper,
                                       String classAttributeIdentifier) {
        super(root, reader, converterLookup, classMapper, classAttributeIdentifier);
    }

    public Object convertAnother(Class type) {
        String reference = reader.getAttribute("reference");
        if (reference != null) {
            return values.get(reference); 
        } else {
            String id = reader.getAttribute("id");
            Object result = super.convertAnother(type);
            if (id != null) {
                values.put(id, result);
            }
            return result;
        }
    }

}
