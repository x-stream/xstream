package com.thoughtworks.xstream.converters.lookup;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;

import java.util.Iterator;
import java.util.LinkedList;

public class DefaultConverterLookup implements ConverterLookup {

    private LinkedList converters = new LinkedList();

    public Converter lookupConverterForType(Class type) {
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
