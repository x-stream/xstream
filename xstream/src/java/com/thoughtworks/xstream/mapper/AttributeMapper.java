/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2013, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 20. February 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.mapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

/**
 * Mapper that allows the usage of attributes for fields and corresponding 
 * types or specified arbitrary types. It is responsible for the lookup of the 
 * {@link SingleValueConverter} for item types and attribute names.
 *
 * @author Paul Hammant 
 * @author Ian Cartwright
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @author Guilherme Silveira
 * @since 1.2
 */
public class AttributeMapper extends MapperWrapper {

    private final Map fieldNameToTypeMap = new HashMap();
    private final Set typeSet = new HashSet();
    private ConverterLookup converterLookup;
    private ReflectionProvider reflectionProvider;
    private final Set fieldToUseAsAttribute = new HashSet();

    /**
     * @deprecated As of 1.3
     */
    public AttributeMapper(Mapper wrapped) {
        this(wrapped, null, null);
    }

    public AttributeMapper(Mapper wrapped, ConverterLookup converterLookup, ReflectionProvider refProvider) {
        super(wrapped);
        this.converterLookup = converterLookup;
        this.reflectionProvider = refProvider;
    }
    
    /**
     * @deprecated As of 1.3
     */
    public void setConverterLookup(ConverterLookup converterLookup) {
        this.converterLookup = converterLookup;
    }

    public void addAttributeFor(final String fieldName, final Class type) {
        fieldNameToTypeMap.put(fieldName, type);
    }

    public void addAttributeFor(final Class type) {
        typeSet.add(type);
    }

    private SingleValueConverter getLocalConverterFromItemType(Class type) {
        Converter converter = converterLookup.lookupConverterForType(type);
        if (converter != null && converter instanceof SingleValueConverter) {
            return (SingleValueConverter)converter;
        } else {
            return null;
        }
    }

    /**
     * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
     */
    public SingleValueConverter getConverterFromItemType(String fieldName, Class type) {
        if (fieldNameToTypeMap.get(fieldName) == type) {
            return getLocalConverterFromItemType(type);
        } else {
            return null;
        }
    }

    public SingleValueConverter getConverterFromItemType(String fieldName, Class type,
        Class definedIn) {
        if (shouldLookForSingleValueConverter(fieldName, type, definedIn)) {
            SingleValueConverter converter = getLocalConverterFromItemType(type);
            if (converter != null) {
                return converter;
            }
        }
        return super.getConverterFromItemType(fieldName, type, definedIn);
    }

    public boolean shouldLookForSingleValueConverter(String fieldName, Class type, Class definedIn) {
        if (typeSet.contains(type)) {
            return true;
        } else if (fieldNameToTypeMap.get(fieldName) == type) {
            return true;
        } else if (fieldName != null && definedIn != null) {
            final Field field = reflectionProvider.getFieldOrNull(definedIn, fieldName);
            return field != null && fieldToUseAsAttribute.contains(field);
        }
        return false;
    }

    /**
     * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
     */
    public SingleValueConverter getConverterFromItemType(Class type) {
        if (typeSet.contains(type)) {
            return getLocalConverterFromItemType(type);
        } else {
            return null;
        }
    }

    /**
     * @deprecated As of 1.3, use {@link #getConverterFromAttribute(Class, String, Class)}
     */
    public SingleValueConverter getConverterFromAttribute(String attributeName) {
        SingleValueConverter converter = null;
        Class type = (Class)fieldNameToTypeMap.get(attributeName);
        if (type != null) {
            converter = getLocalConverterFromItemType(type);
        }
        return converter;
    }

    /**
     * @deprecated As of 1.3.1, use {@link #getConverterFromAttribute(Class, String, Class)}
     */
    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute) {
        final Field field = reflectionProvider.getFieldOrNull(definedIn, attribute);
        return field != null ? getConverterFromAttribute(definedIn, attribute, field.getType()) : null;
    }

    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
        if (shouldLookForSingleValueConverter(attribute, type, definedIn)) {
            SingleValueConverter converter = getLocalConverterFromItemType(type);
            if (converter != null) {
                return converter;
            }
        }
        return super.getConverterFromAttribute(definedIn, attribute, type);
    }

    /**
     * Tells this mapper to use an attribute for this field.
     * 
     * @param field the field itself
     * @since 1.2.2
     */
    public void addAttributeFor(final Field field) {
        if (field != null) {
            fieldToUseAsAttribute.add(field);
        }
    }

    /**
     * Tells this mapper to use an attribute for this field.
     * 
     * @param definedIn the declaring class of the field
     * @param fieldName the name of the field
     * @since 1.3
     */
    public void addAttributeFor(Class definedIn, String fieldName) {
        addAttributeFor(reflectionProvider.getField(definedIn, fieldName));
    }
}
