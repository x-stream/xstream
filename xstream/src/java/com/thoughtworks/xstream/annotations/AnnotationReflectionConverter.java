/*
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. March 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.util.DependencyInjectionFactory;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * ReflectionConverter which uses an AnnotationProvider to marshall and unmarshall fields based
 * on the annotated converters.
 * 
 * @author Guilherme Silveira
 * @author Mauro Talevi
 * @deprecated since 1.3, build into {@link ReflectionConverter}
 */
@Deprecated
public class AnnotationReflectionConverter extends ReflectionConverter {

    private final AnnotationProvider annotationProvider;

    private final Map<Class<? extends ConverterMatcher>, Converter> cachedConverters;

    @Deprecated
    public AnnotationReflectionConverter(
                                         Mapper mapper, ReflectionProvider reflectionProvider,
                                         AnnotationProvider annotationProvider) {
        super(mapper, reflectionProvider);
        this.annotationProvider = annotationProvider;
        this.cachedConverters = new HashMap<Class<? extends ConverterMatcher>, Converter>();
    }

    protected void marshallField(final MarshallingContext context, Object newObj, Field field) {
        XStreamConverter annotation = annotationProvider.getAnnotation(
            field, XStreamConverter.class);
        if (annotation != null) {
            Class<? extends ConverterMatcher> type = annotation.value();
            ensureCache(type);
            context.convertAnother(newObj, cachedConverters.get(type));
        } else {
            context.convertAnother(newObj);
        }
    }

    private void ensureCache(Class<? extends ConverterMatcher> type) {
        if (!this.cachedConverters.containsKey(type)) {
            cachedConverters.put(type, newInstance(type));
        }
    }

    protected Object unmarshallField(
                                     final UnmarshallingContext context, final Object result,
                                     Class type, Field field) {
        XStreamConverter annotation = annotationProvider.getAnnotation(
            field, XStreamConverter.class);
        if (annotation != null) {
            Class<? extends Converter> converterType = (Class<? extends Converter>)annotation.value();
            ensureCache(converterType);
            return context.convertAnother(result, type, cachedConverters.get(converterType));
        } else {
            return context.convertAnother(result, type);
        }
    }

    /**
     * Instantiates a converter using its default constructor.
     * 
     * @param type the converter type to instantiate
     * @return the new instance
     */
    private Converter newInstance(Class<? extends ConverterMatcher> type) {
        Converter converter;
        // TODO: We need a separate exception for runtime initialization.
        try {
            if (SingleValueConverter.class.isAssignableFrom(type)) {
                final SingleValueConverter svc = (SingleValueConverter)type.getConstructor().newInstance();
                converter = new SingleValueConverterWrapper(svc);
            } else {
                converter = (Converter)type.getConstructor().newInstance();
            }
        } catch (InvocationTargetException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e
                .getCause());
        } catch (InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (NoSuchMethodException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        }
        return converter;
    }

}
