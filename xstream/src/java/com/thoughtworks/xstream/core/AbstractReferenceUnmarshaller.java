package com.thoughtworks.xstream.core;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Abstract base class for a TreeUnmarshaller, that resolves refrences.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class AbstractReferenceUnmarshaller extends TreeUnmarshaller {

    private Map values = new HashMap();
    private FastStack parentStack = new FastStack(16);

    public AbstractReferenceUnmarshaller(Object root, HierarchicalStreamReader reader,
                                     ConverterLookup converterLookup, Mapper mapper) {
        super(root, reader, converterLookup, mapper);
    }

    protected Object convert(Object parent, Class type, Converter converter) {
        if (parentStack.size() > 0) { // handles circular references
            Object parentReferenceKey = parentStack.peek();
            if (parentReferenceKey != null) {
                if (!values.containsKey(parentReferenceKey)) { // see AbstractCircularReferenceTest.testWeirdCircularReference()
                    values.put(parentReferenceKey, parent);
                }
            }
        }
        String reference = reader.getAttribute(getMapper().aliasForAttribute("reference"));
        if (reference != null) {
            return values.get(getReferenceKey(reference));
        } else {
            Object currentReferenceKey = getCurrentReferenceKey();
            parentStack.push(currentReferenceKey);
            Object result = super.convert(parent, type, converter);
            if (currentReferenceKey != null) {
                values.put(currentReferenceKey, result);
            }
            parentStack.popSilently();
            return result;
        }
    }
    
    protected abstract Object getReferenceKey(String reference);
    protected abstract Object getCurrentReferenceKey();
}
