package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.StringStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.util.HashMap;
import java.util.Map;

public class ReferenceByIdUnmarshaller extends TreeUnmarshaller {

    private Map values = new HashMap();
    private StringStack parentIdStack = new StringStack(16);

    public ReferenceByIdUnmarshaller(Object root, HierarchicalStreamReader reader,
                                     ConverterLookup converterLookup, ClassMapper classMapper,
                                     String classAttributeIdentifier) {
        super(root, reader, converterLookup, classMapper, classAttributeIdentifier);
    }

    public Object convertAnother(Class type) {
        throw new UnsupportedOperationException();
    }

    public Object convertAnother(Object parent, Class type) {
        if (parentIdStack.size() > 0) { // handles circular references
            values.put(parentIdStack.peek(), parent);
        }
        String reference = reader.getAttribute("reference");
        if (reference != null) {
            return values.get(reference);
        } else {
            String currentId = reader.getAttribute("id");
            parentIdStack.push(currentId);
            Object result = super.convertAnother(parent, type);
            values.put(currentId, result);
            parentIdStack.popSilently();
            return result;
        }
    }

}
