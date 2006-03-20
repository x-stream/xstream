package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.SingleValueConverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper that allows aliasing of attribute names and corresponding types.
 * It is responsible for the lookup of the {@link SingleValueConverter}
 * for item types and attribute names.
 *
 * @author Paul Hammant 
 * @author Ian Cartwright
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @since 1.2
 */
public class AttributeAliasingMapper extends MapperWrapper {

    protected final Map nameToTypeMap = Collections.synchronizedMap(new HashMap());
    private ConverterLookup converterLookup;

    public AttributeAliasingMapper(Mapper wrapped) {
        this(wrapped, null);
    }

    public AttributeAliasingMapper(Mapper wrapped, ConverterLookup converterLookup) {
        super(wrapped);
        this.converterLookup = converterLookup;
    }
    
    // TODO: Is this needed now that it injected in ctor? - MT
    // YES, but I want to remove it. In the ctor NULL is injected!!! - JS
    public void setConverterLookup(ConverterLookup converterLookup) {
        this.converterLookup = converterLookup;
    }

    public void addAttributeAlias(final String attributeName, final Class type) {
        nameToTypeMap.put(attributeName, type);
    }

    protected SingleValueConverter getLocalConverterFromItemType(Class type) {
        Converter converter = converterLookup.lookupConverterForType(type);
        if (converter != null && converter instanceof SingleValueConverter) {
            return (SingleValueConverter)converter;
        } else {
            return null;
        }
    }

    public SingleValueConverter getConverterFromItemType(Class type) {
        if (nameToTypeMap.containsValue(type)) {
            return getLocalConverterFromItemType(type);
        } else {
            return null;
        }
    }

    public SingleValueConverter getConverterFromAttribute(String attributeName) {
        SingleValueConverter converter = null;
        Class type = (Class)nameToTypeMap.get(attributeName);
        if (type != null) {
            converter = getLocalConverterFromItemType(type);
        }
        return converter;
    }

}
