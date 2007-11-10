/*
 * Copyright (C) 2007 XStream Committers.
 * Created on 07.11.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.core.util.DependencyInjectionFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * A mapper that uses annotations to prepare the remaining mappers in the chain.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class AnnotationMapper extends MapperWrapper implements AnnotationConfiguration {

    private boolean locked; // false for now
    private final Object[] arguments = new Object[0]; // no args for now
    private final LocalConversionMapper localConversionMapper;
    private final Map<String, Converter> converterCache = new HashMap<String, Converter>();
    private final Set<String> annotatedTypes = new HashSet<String>();

    /**
     * Construct an AnnotationMapper.
     * 
     * @param wrapped the next {@link Mapper} in the chain
     * @since upcoming
     */
    public AnnotationMapper(final Mapper wrapped) {
        super(wrapped);
        localConversionMapper = (LocalConversionMapper)lookupMapperOfType(LocalConversionMapper.class);
    }

    @Override
    public Converter getLocalConverter(final Class definedIn, final String fieldName) {
        if (!locked) {
            processAnnotations(definedIn);
        }
        return super.getLocalConverter(definedIn, fieldName);
    }

    public void processAnnotations(final Class type) {
        if (type == null) {
            return;
        }
        synchronized (annotatedTypes) {
            if (!annotatedTypes.contains(type.getName())) {
                annotatedTypes.add(type.getName());
                final Field[] fields = type.getDeclaredFields();
                for (int i = 0; i < fields.length; i++ ) {
                    final XStreamConverter annotation = fields[i]
                        .getAnnotation(XStreamConverter.class);
                    if (annotation != null) {
                        final Class<? extends Converter> converterType = annotation.value();
                        Converter converter = converterCache.get(converterType.getName());
                        if (converter == null) {
                            try {
                                converter = (Converter)DependencyInjectionFactory.newInstance(
                                    converterType, arguments);
                                converterCache.put(converterType.getName(), converter);
                            } catch (final Exception e) {
                                // ignore, no converter though
                            }
                        }
                        if (converter != null) {
                            localConversionMapper.registerLocalConverter(type, fields[i]
                                .getName(), converter);
                        }
                    }
                }
            }
        }
    }
}
