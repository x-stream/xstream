package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.util.HashMap;
import java.util.Map;

public class ReferenceByIdUnmarshaller extends TreeUnmarshaller {

    private Map values = new HashMap();
    private String lastId;

    public ReferenceByIdUnmarshaller(Object root, HierarchicalStreamReader reader,
                                       ConverterLookup converterLookup, ClassMapper classMapper,
                                       String classAttributeIdentifier) {
        super(root, reader, converterLookup, classMapper, classAttributeIdentifier);
    }

    public Object convertAnother(Class type) {
        throw new UnsupportedOperationException();
    }

    public Object convertAnother(Object current, Class type) {
        if (lastId != null) { // handles circular references
            values.put(lastId, current);
        }
        String reference = reader.getAttribute("reference");
        if (reference != null) {
            return values.get(reference);
        } else {
            lastId = reader.getAttribute("id");
            Object result = super.convertAnother(current, type);
//            if (lastId != null) {
//                values.put(lastId, result);
//            }
            return result;
        }
    }

}
