/*
 * Copyright (C) 2006, 2007, 2014, 2016, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. July 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.converters.extended;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;


/**
 * Convenient converter for classes with natural string representation.
 * <p>
 * Converter for classes that adopt the following convention: - a constructor that takes a single string parameter - a
 * toString() that is overloaded to issue a string that is meaningful
 * </p>
 *
 * @author Paul Hammant
 */
public class ToStringConverter extends AbstractSingleValueConverter {
    private final Class<?> clazz;
    private final Constructor<?> ctor;

    public ToStringConverter(final Class<?> clazz) throws NoSuchMethodException {
        this.clazz = clazz;
        ctor = clazz.getConstructor(String.class);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == clazz;
    }

    @Override
    public String toString(final Object obj) {
        return obj == null ? null : obj.toString();
    }

    @Override
    public Object fromString(final String str) {
        try {
            return ctor.newInstance(str);
        } catch (final InstantiationException e) {
            throw new ConversionException("Unable to instantiate single String param constructor", e);
        } catch (final IllegalAccessException e) {
            throw new ObjectAccessException("Unable to access single String param constructor", e);
        } catch (final InvocationTargetException e) {
            throw new ConversionException("Unable to target single String param constructor", e.getTargetException());
        }
    }
}
