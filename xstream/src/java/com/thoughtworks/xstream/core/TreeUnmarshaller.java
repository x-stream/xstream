package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.ClassStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.XStream;

public class TreeUnmarshaller implements UnmarshallingContext {

    private Object root;
    protected HierarchicalStreamReader reader;
    private ConverterLookup converterLookup;
    private ClassMapper classMapper;
    private String classAttributeIdentifier;
    private ClassStack types = new ClassStack(16);

    public TreeUnmarshaller(Object root, HierarchicalStreamReader reader,
                                       ConverterLookup converterLookup, ClassMapper classMapper,
                                       String classAttributeIdentifier) {
        this.root = root;
        this.reader = reader;
        this.converterLookup = converterLookup;
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
    }

    /** @deprecated */
    public Object convertAnother(Class type) {
        return convertAnother(null, type);
    }

    public Object convertAnother(Object current, Class type) {
        Converter converter = converterLookup.lookupConverterForType(type);
        types.push(type);
        Object result = converter.unmarshal(reader, this);
        types.popSilently();
        return result;
    }

    public Object currentObject() {
        return root;
    }

    public Class getRequiredType() {
        return types.peek();
    }

    public Object start() {
        String classAttribute = reader.getAttribute(classAttributeIdentifier);
        Class type;
        if (classAttribute == null) {
            type = classMapper.lookupType(reader.getNodeName());
        } else {
            type = classMapper.lookupType(classAttribute);
        }
        return convertAnother(root, type);
    }

}
