package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.util.HashMap;
import java.util.Map;

public class ReferenceByIdUnmarshaller extends TreeUnmarshaller {

    private Map values = new HashMap();
    private FastStack parentIdStack = new FastStack(16);

    public ReferenceByIdUnmarshaller(Object root, HierarchicalStreamReader reader,
                                     ConverterLookup converterLookup, ClassMapper classMapper) {
        super(root, reader, converterLookup, classMapper);
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
