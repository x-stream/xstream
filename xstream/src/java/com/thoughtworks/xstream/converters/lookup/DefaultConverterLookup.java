package com.thoughtworks.xstream.converters.lookup;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.basic.*;
import com.thoughtworks.xstream.converters.collections.ListConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.composite.ObjectWithFieldsConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class DefaultConverterLookup implements ConverterLookup {

    private LinkedList converters = new LinkedList();

    public DefaultConverterLookup(ClassMapper classMapper) {

        registerConverter(new ObjectWithFieldsConverter(classMapper));

        registerConverter(new IntConverter());
        registerConverter(new FloatConverter());
        registerConverter(new DoubleConverter());
        registerConverter(new LongConverter());
        registerConverter(new ShortConverter());
        registerConverter(new CharConverter());
        registerConverter(new BooleanConverter());
        registerConverter(new ByteConverter());

        registerConverter(new StringConverter());
        registerConverter(new StringBufferConverter());
        registerConverter(new DateConverter());

        registerConverter(new ListConverter(classMapper, ArrayList.class));
        registerConverter(new MapConverter(classMapper, HashMap.class));
    }

    public Converter lookup(Class type) {
        for (Iterator iterator = converters.iterator(); iterator.hasNext();) {
            Converter converter = (Converter) iterator.next();
            if (converter.canConvert(type)) {
                return converter;
            }
        }
        throw new ConversionException("No converter specified for " + type);
    }

    public void registerConverter(Converter converter) {
        converters.addFirst(converter);
    }

}
