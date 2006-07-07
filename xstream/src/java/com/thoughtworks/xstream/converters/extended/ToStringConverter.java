package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Convenient converter for classes with natural string representation.
 * 
 * Converter for classes that adopt the following convention:
 *   - a constructor that takes a single string parameter
 *   - a toString() that is overloaded to issue a string that is meaningful
 *
 * @author Paul Hammant
 */
public class ToStringConverter extends AbstractSingleValueConverter {
    private final Class clazz;
    private final Constructor ctor;

    public ToStringConverter(Class clazz) throws NoSuchMethodException {
        this.clazz = clazz;
        ctor = clazz.getConstructor(new Class[] {String.class});
    }
    public boolean canConvert(Class type) {
        return type.equals(clazz);
    }
    public String toString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    public Object fromString(String str) {
        try {
            return ctor.newInstance(new Object[] {str});
        } catch (InstantiationException e) {
            throw new ConversionException("Unable to instantiate single String param constructor", e);
        } catch (IllegalAccessException e) {
            throw new ConversionException("Unable to access single String param constructor", e);
        } catch (InvocationTargetException e) {
            throw new ConversionException("Unable to target single String param constructor", e.getTargetException());
        }
    }
}
