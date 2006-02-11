package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.PrioritizedList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultConverterLookup implements ConverterLookup {

    private final PrioritizedList converters = new PrioritizedList();
    private final Map typeToConverterMap = Collections.synchronizedMap(new HashMap());
    private final ClassMapper classMapper;

    public DefaultConverterLookup(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    /**
     * @deprecated As of 1.1.1 you can register Converters with priorities, making the need for a default converter redundant.
     */
    public Converter defaultConverter() {
        return (Converter) converters.firstOfLowestPriority();
    }

    public Converter lookupConverterForType(Class type) {
        Converter cachedConverter = (Converter) typeToConverterMap.get(type);
        if (cachedConverter != null) return cachedConverter;
        Class mapType = classMapper.defaultImplementationOf(type);
        Iterator iterator = converters.iterator();
        while (iterator.hasNext()) {
            Converter converter = (Converter) iterator.next();
            if (converter.canConvert(mapType)) {
                typeToConverterMap.put(type, converter);
                return converter;
            }
        }
        throw new ConversionException("No converter specified for " + type);
    }
    
    public void registerConverter(Converter converter, int priority) {
        converters.add(converter, priority);
    }

}
