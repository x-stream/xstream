package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.core.util.ClassStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;

public class TreeUnmarshaller implements UnmarshallingContext {

    private Object root;
    protected HierarchicalStreamReader reader;
    private ConverterLookup converterLookup;
    private ClassMapper classMapper;
    private String classAttributeIdentifier;
    private ClassStack types = new ClassStack(16);
    private Map data;

    public TreeUnmarshaller(Object root, HierarchicalStreamReader reader,
                            ConverterLookup converterLookup, ClassMapper classMapper,
                            String classAttributeIdentifier) {
        this.root = root;
        this.reader = reader;
        this.converterLookup = converterLookup;
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
    }

    public Object convertAnother(Object parent, Class type) {
        try {
            Converter converter = converterLookup.lookupConverterForType(type);
            types.push(type);
            Object result = converter.unmarshal(reader, this);
            types.popSilently();
            return result;
        } catch (ConversionException conversionException) {
            addInformationTo(conversionException, type);
            throw conversionException;
        } catch (RuntimeException e) {
            ConversionException conversionException = new ConversionException(e);
            addInformationTo(conversionException, type);
            throw conversionException;
        }
    }

    private void addInformationTo(ErrorWriter errorWriter, Class type) {
        errorWriter.add("class", type.getName());
        errorWriter.add("required-type", getRequiredType().getName());
        reader.appendErrors(errorWriter);
    }

    public Object currentObject() {
        return types.size() == 1 ? root : null;
    }

    public Class getRequiredType() {
        return types.peek();
    }

    public Object get(Object key) {
        initData();
        return data.get(key);
    }

    public void put(Object key, Object value) {
        initData();
        data.put(key, value);
    }

    public Iterator keys() {
        initData();
        return Collections.unmodifiableCollection(data.keySet()).iterator();
    }

    private void initData() {
        if (data == null) {
            data = new HashMap();
        }
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
