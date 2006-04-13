package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.PrioritizedList;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The default implementation of converters lookup.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 */
public class DefaultConverterLookup implements ConverterLookup {

    private final PrioritizedList converters = new PrioritizedList();
    private transient Map typeToConverterMap = Collections.synchronizedMap(new HashMap());
    private final Mapper mapper;

    public DefaultConverterLookup(Mapper mapper) {
        this.mapper = mapper;
    }

    /**
     * @deprecated As of 1.2, use {@link #DefaultConverterLookup(Mapper)}
     */
    public DefaultConverterLookup(ClassMapper classMapper) {
        this((Mapper)classMapper);
    }

    public Converter lookupConverterForType(Class type) {
        Converter cachedConverter = (Converter) typeToConverterMap.get(type);
        if (cachedConverter != null) return cachedConverter;
        Class mapType = mapper.defaultImplementationOf(type);
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
        for (Iterator iter = this.typeToConverterMap.keySet().iterator(); iter.hasNext();) {
            Class type = (Class) iter.next();
            if (converter.canConvert(type)) {
                iter.remove();
            }
        }
    }
    
    private Object readResolve() {
        typeToConverterMap = Collections.synchronizedMap(new HashMap());
        return this;
    }

}
