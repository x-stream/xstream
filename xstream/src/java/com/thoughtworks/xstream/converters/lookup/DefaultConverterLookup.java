package com.thoughtworks.xstream.converters.lookup;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.basic.*;
import com.thoughtworks.xstream.converters.collections.ListConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.composite.ObjectWithFieldsConverter;

import java.util.*;

public class DefaultConverterLookup implements ConverterLookup {

    private Converter defaultConverter;
    private Map map = new HashMap();

    public DefaultConverterLookup(ClassMapper classMapper) {
        this.defaultConverter = new ObjectWithFieldsConverter(classMapper);

        registerConverterForClass(new IntConverter(), int.class);
        registerConverterForClass(new IntConverter(), Integer.class);
        registerConverterForClass(new FloatConverter(), float.class);
        registerConverterForClass(new FloatConverter(), Float.class);
        registerConverterForClass(new DoubleConverter(), double.class);
        registerConverterForClass(new DoubleConverter(), Double.class);
        registerConverterForClass(new LongConverter(), long.class);
        registerConverterForClass(new LongConverter(), Long.class);
        registerConverterForClass(new ShortConverter(), short.class);
        registerConverterForClass(new ShortConverter(), Short.class);
        registerConverterForClass(new CharConverter(), char.class);
        registerConverterForClass(new CharConverter(), Character.class);
        registerConverterForClass(new BooleanConverter(), boolean.class);
        registerConverterForClass(new BooleanConverter(), Boolean.class);
        registerConverterForClass(new ByteConverter(), byte.class);
        registerConverterForClass(new ByteConverter(), Byte.class);

        registerConverterForClass(new StringConverter(), String.class);
        registerConverterForClass(new DateConverter(), Date.class);

        registerConverterForClass(new ListConverter(classMapper, ArrayList.class), List.class);
        registerConverterForClass(new MapConverter(classMapper, HashMap.class), Map.class);

        // @TODO: find a more elegant way of doing this
        registerConverterForClass(new ListConverter(classMapper, ArrayList.class), ArrayList.class);
        registerConverterForClass(new ListConverter(classMapper, LinkedList.class), LinkedList.class);
        registerConverterForClass(new MapConverter(classMapper, HashMap.class), HashMap.class);
        registerConverterForClass(new MapConverter(classMapper, TreeMap.class), TreeMap.class);
    }

    public Converter lookup(Class fieldType) {
        Converter result = (Converter) map.get(fieldType);
        if (result == null) {
            result = defaultConverter;
        }
        return result;
    }

    public void registerConverterForClass(Converter converter, Class type) {
        map.put(type, converter);
    }

}
