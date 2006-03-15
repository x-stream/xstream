package com.thoughtworks.xstream.core;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

public class ReferenceByIdUnmarshaller extends TreeUnmarshaller {

    private Map values = new HashMap();
    private FastStack parentIdStack = new FastStack(16);

    public ReferenceByIdUnmarshaller(Object root, HierarchicalStreamReader reader,
                                     ConverterLookup converterLookup, Mapper mapper) {
        super(root, reader, converterLookup, mapper);
    }

    /**
     * @deprecated As of 1.2, use {@link #ReferenceByIdUnmarshaller(Object, HierarchicalStreamReader, ConverterLookup, Mapper)}
     */
    public ReferenceByIdUnmarshaller(Object root, HierarchicalStreamReader reader,
                                     ConverterLookup converterLookup, ClassMapper classMapper) {
        super(root, reader, converterLookup, classMapper);
    }

    protected Object convert(Object parent, Class type, Converter converter) {
        if (parentIdStack.size() > 0) { // handles circular references
            Object parentId = parentIdStack.peek();
            //System.out.print("Stacked ID: " + parentId + " <" + System.identityHashCode(parent) + ":" + parent.toString() + ">\n");
            if (!values.containsKey(parentId)) { // see AbstractCircularReferenceTest.testWeirdCircularReference()
                values.put(parentId, parent);
            }
        }
        String reference = reader.getAttribute("reference");
        if (reference != null) {
            //System.out.print("Pick ID: " + reference + "\n");
            return values.get(reference);
        } else {
            String currentId = reader.getAttribute("id");
            parentIdStack.push(currentId);
            Object result = super.convert(parent, type, converter);
            //System.out.print("Current ID: " + currentId + " <" + System.identityHashCode(result) + ":" + result.toString() + ">\n");
            values.put(currentId, result);
            parentIdStack.popSilently();
            return result;
        }
    }

    
}
