package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.basic.NullConverter;

import java.util.ArrayList;
import java.util.HashMap;

public class DefaultConverterLookup implements ConverterLookup {

    private ArrayList converters = new ArrayList();
    private Converter nullConverter = new NullConverter();
    private HashMap typeToConverterMap = new HashMap();
    private ClassMapper classMapper;
    private Converter defaultConverter;

    public DefaultConverterLookup(Converter defaultConverter, ClassMapper classMapper) {
        this.defaultConverter = defaultConverter;
        this.classMapper = classMapper;
    }

    public Converter defaultConverter() {
        return defaultConverter;
    }
    
    public void changeDefaultConverter(Converter newDefaultConverter) {
        defaultConverter = newDefaultConverter;
    }

    public Converter lookupConverterForType(Class type) {
        if (type == null) {
            return nullConverter;
        }
        Converter cachedConverter = (Converter) typeToConverterMap.get(type);
        if (cachedConverter != null) return cachedConverter;
        type = classMapper.defaultImplementationOf(type);
        int size = converters.size();
        for (int i = size - 1; i >= 0; i--) {
            Converter converter = (Converter) converters.get(i);
            if (converter.canConvert(type)) {
                typeToConverterMap.put(type, converter);
                return converter;
            }
        }
        throw new ConversionException("No converter specified for " + type);
    }

    public void registerConverter(Converter converter) {
        converters.add(converter);
    }

}
