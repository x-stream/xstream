package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.PrioritizedList;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Iterator;

public class TreeUnmarshaller implements UnmarshallingContext {

    private Object root;
    protected HierarchicalStreamReader reader;
    private ConverterLookup converterLookup;
    private Mapper mapper;
    private FastStack types = new FastStack(16);
    private DataHolder dataHolder;
    private final PrioritizedList validationList = new PrioritizedList();

    public TreeUnmarshaller(Object root, HierarchicalStreamReader reader,
                            ConverterLookup converterLookup, Mapper mapper) {
        this.root = root;
        this.reader = reader;
        this.converterLookup = converterLookup;
        this.mapper = mapper;
    }

    /**
     * @deprecated As of 1.2, use {@link #TreeUnmarshaller(Object, HierarchicalStreamReader, ConverterLookup, Mapper)}
     */
    public TreeUnmarshaller(Object root, HierarchicalStreamReader reader,
                            ConverterLookup converterLookup, ClassMapper classMapper) {
        this(root, reader, converterLookup, (Mapper)classMapper);
    }


    public Object convertAnother(Object parent, Class type) {
        Converter converter = converterLookup.lookupConverterForType(type);
        return convert(parent, type,converter);
    }

    public Object convertAnother(Object parent, Class type, Converter converter) {
        return convert(parent, type, converter);
    }

    protected Object convert(Object parent, Class type, Converter converter) {
        try {
            types.push(mapper.defaultImplementationOf(type));
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

    public void addCompletionCallback(Runnable work, int priority) {
        validationList.add(work, priority);
    }

    public Object currentObject() {
        return types.size() == 1 ? root : null;
    }

    public Class getRequiredType() {
        return (Class)types.peek();
    }

    public Object get(Object key) {
        lazilyCreateDataHolder();
        return dataHolder.get(key);
    }

    public void put(Object key, Object value) {
        lazilyCreateDataHolder();
        dataHolder.put(key, value);
    }

    public Iterator keys() {
        lazilyCreateDataHolder();
        return dataHolder.keys();
    }

    private void lazilyCreateDataHolder() {
        if (dataHolder == null) {
            dataHolder = new MapBackedDataHolder();
        }
    }

    public Object start(DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        String classAttribute = reader.getAttribute(mapper.aliasForAttribute("class"));
        Class type;
        if (classAttribute == null) {
            type = mapper.realClass(reader.getNodeName());
        } else {
            type = mapper.realClass(classAttribute);
        }
        Object result = convertAnother(null, type);
        Iterator validations = validationList.iterator();
        while (validations.hasNext()) {
            Runnable runnable = (Runnable) validations.next();
            runnable.run();
        }
        return result;
    }

    protected Mapper getMapper() {
        return this.mapper;
    }

}
