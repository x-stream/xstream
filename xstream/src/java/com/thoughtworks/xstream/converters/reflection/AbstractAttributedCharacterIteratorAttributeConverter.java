package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * An abstract converter implementation for constants of
 * {@link AttributedCharacterIterator.Attribute} and derived types.
 * 
 * @author J&ouml;rg Schaible
 * since 1.2.2
 */
public class AbstractAttributedCharacterIteratorAttributeConverter extends
    AbstractSingleValueConverter {

    private static final Method getName;
    static {
        try {
            getName = AttributedCharacterIterator.Attribute.class.getDeclaredMethod(
                "getName", (Class[])null);
        } catch (NoSuchMethodException e) {
            throw new InternalError("Missing AttributedCharacterIterator.Attribute.getName()");
        }
    }

    private final Class type;
    private transient Map attributeMap;
    private transient FieldDictionary fieldDictionary;

    public AbstractAttributedCharacterIteratorAttributeConverter(final Class type) {
        super();
        this.type = type;
        readResolve();
    }

    public boolean canConvert(final Class type) {
        return type == this.type;
    }

    public String toString(final Object source) {
        AttributedCharacterIterator.Attribute attribute = (AttributedCharacterIterator.Attribute)source;
        try {
            if (!getName.isAccessible()) {
                getName.setAccessible(true);
            }
            return (String)getName.invoke(attribute, (Object[])null);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException(
                "Cannot get name of AttributedCharacterIterator.Attribute", e);
        } catch (InvocationTargetException e) {
            throw new ObjectAccessException(
                "Cannot get name of AttributedCharacterIterator.Attribute", e
                    .getTargetException());
        }
    }

    public Object fromString(final String str) {
        return attributeMap.get(str);
    }

    private Object readResolve() {
        fieldDictionary = new FieldDictionary();
        attributeMap = new HashMap();
        for (final Iterator iterator = fieldDictionary.serializableFieldsFor(type); iterator
            .hasNext();) {
            final Field field = (Field)iterator.next();
            if (field.getType() == type && Modifier.isStatic(field.getModifiers())) {
                try {
                    final Object attribute = field.get(null);
                    attributeMap.put(toString(attribute), attribute);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Cannot get object of " + field, e);
                }
            }
        }
        return this;
    }

}
