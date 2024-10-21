/*
 * Copyright (C) 2011, 2013, 2014, 2015, 2016, 2024 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. July 2011 by Joerg Schaible
 */

package com.thoughtworks.xstream.converters.extended;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter.DuplicateFieldException;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.MemberDictionary;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converter that supports the definition of one field member that will be written as value and all other field members
 * are written as attributes.
 * <p>
 * The converter requires that all the field types (expect the one with the value) are handled by a
 * {@link SingleValueConverter}. The value field is defined using the name of the type that declares the field and the
 * field name itself. Therefore it is possible to define an inherited field as value. It is also possible to provide no
 * value field at all, so that all fields are written as attributes.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class ToAttributedValueConverter implements Converter {
    private static final String STRUCTURE_MARKER = "";
    private final Class<?> type;
    private final Mapper mapper;
    private final Mapper enumMapper;
    private final ReflectionProvider reflectionProvider;
    private final ConverterLookup lookup;
    private final Field valueField;

    /**
     * Creates a new ToAttributedValueConverter instance. All field elements will be attributes, the element itself will
     * have no value.
     *
     * @param type the type that is handled by this converter instance
     * @param mapper the mapper in use
     * @param reflectionProvider the reflection provider in use
     * @param lookup the converter lookup in use
     * @since 1.4.9
     */
    public ToAttributedValueConverter(
            final Class<?> type, final Mapper mapper, final ReflectionProvider reflectionProvider,
            final ConverterLookup lookup) {
        this(type, mapper, reflectionProvider, lookup, null, null);
    }

    /**
     * Creates a new ToAttributedValueConverter instance.
     *
     * @param type the type that is handled by this converter instance
     * @param mapper the mapper in use
     * @param reflectionProvider the reflection provider in use
     * @param lookup the converter lookup in use
     * @param valueFieldName the field defining the tag's value (may be null)
     */
    public ToAttributedValueConverter(
            final Class<?> type, final Mapper mapper, final ReflectionProvider reflectionProvider,
            final ConverterLookup lookup, final String valueFieldName) {
        this(type, mapper, reflectionProvider, lookup, valueFieldName, null);
    }

    /**
     * Creates a new ToAttributedValueConverter instance.
     *
     * @param type the type that is handled by this converter instance
     * @param mapper the mapper in use
     * @param reflectionProvider the reflection provider in use
     * @param lookup the converter lookup in use
     * @param valueFieldName the field defining the tag's value (may be null)
     * @param valueDefinedIn the type defining the field
     */
    public ToAttributedValueConverter(
            final Class<?> type, final Mapper mapper, final ReflectionProvider reflectionProvider,
            final ConverterLookup lookup, final String valueFieldName, final Class<?> valueDefinedIn) {
        this.type = type;
        this.mapper = mapper;
        this.reflectionProvider = reflectionProvider;
        this.lookup = lookup;

        if (valueFieldName == null) {
            valueField = null;
        } else {
            Field field = null;
            try {
                field = (valueDefinedIn != null ? valueDefinedIn : type).getDeclaredField(valueFieldName);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
            } catch (final NoSuchFieldException e) {
                throw new IllegalArgumentException(e.getMessage() + ": " + valueFieldName);
            }
            valueField = field;
        }
        enumMapper = UseAttributeForEnumMapper.createEnumMapper(mapper);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return this.type == type;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Class<?> sourceType = source.getClass();
        final Map<String, Field> defaultFieldDefinition = new HashMap<>();
        final String[] tagValue = new String[1];
        final Object[] realValue = new Object[1];
        final Class<?>[] fieldType = new Class[1];
        final Class<?>[] definingType = new Class[1];
        reflectionProvider.visitSerializableFields(source, new ReflectionProvider.Visitor() {
            @Override
            public void visit(final String fieldName, final Class<?> type, final Class<?> definedIn,
                    final Object value) {
                if (!mapper.shouldSerializeMember(definedIn, fieldName)) {
                    return;
                }

                final String alias = mapper.serializedMember(definedIn, fieldName);
                if (!defaultFieldDefinition.containsKey(alias)) {
                    final Class<?> lookupType = sourceType;
                    defaultFieldDefinition.put(alias, reflectionProvider.getField(lookupType, fieldName));
                } else if (!fieldIsEqual(definedIn, fieldName)) {
                    final ConversionException exception = new ConversionException(
                        "Cannot write attribute twice for object");
                    exception.add("alias", alias);
                    exception.add("type", sourceType.getName());
                    throw exception;
                }

                ConverterMatcher converter = Enum.class.isAssignableFrom(type)
                    ? (ConverterMatcher)enumMapper.getConverterFromItemType(null, type, null)
                    : (ConverterMatcher)mapper.getLocalConverter(definedIn, fieldName);
                if (converter == null) {
                    converter = lookup.lookupConverterForType(type);
                }

                if (value != null) {
                    final boolean isValueField = valueField != null && fieldIsEqual(definedIn, fieldName);
                    if (isValueField) {
                        definingType[0] = definedIn;
                        fieldType[0] = type;
                        realValue[0] = value;
                        tagValue[0] = STRUCTURE_MARKER;
                    }
                    if (converter instanceof SingleValueConverter) {
                        final String str = ((SingleValueConverter)converter).toString(value);

                        if (isValueField) {
                            tagValue[0] = str;
                        } else {
                            if (str != null) {
                                writer.addAttribute(alias, str);
                            }
                        }
                    } else {
                        if (!isValueField) {
                            final ConversionException exception = new ConversionException(
                                "Cannot write element as attribute");
                            exception.add("alias", alias);
                            exception.add("type", sourceType.getName());
                            throw exception;
                        }
                    }
                }
            }
        });

        if (tagValue[0] != null) {
            final Class<?> actualType = realValue[0].getClass();
            final Class<?> defaultType = mapper.defaultImplementationOf(fieldType[0]);
            if (!actualType.equals(defaultType)) {
                final String serializedClassName = mapper.serializedClass(actualType);
                if (!serializedClassName.equals(mapper.serializedClass(defaultType))) {
                    final String attributeName = mapper.aliasForSystemAttribute("class");
                    if (attributeName != null) {
                        writer.addAttribute(attributeName, serializedClassName);
                    }
                }
            }

            if (tagValue[0] == STRUCTURE_MARKER) {
                context.convertAnother(realValue[0]);
            } else {
                writer.setValue(tagValue[0]);
            }
        }
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Object result = reflectionProvider.newInstance(context.getRequiredType());
        final Class<?> resultType = result.getClass();

        final MemberDictionary seenFields = new MemberDictionary();
        final Iterator<String> it = reader.getAttributeNames();

        final Set<String> systemAttributes = new HashSet<>();
        systemAttributes.add(mapper.aliasForSystemAttribute("class"));

        // Process attributes before recursing into child elements.
        while (it.hasNext()) {
            final String attrName = it.next();
            if (systemAttributes.contains(attrName)) {
                continue;
            }

            final String fieldName = mapper.realMember(resultType, attrName);
            final Field field = reflectionProvider.getFieldOrNull(resultType, fieldName);
            if (field != null) {
                if (Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                Class<?> type = field.getType();
                final Class<?> declaringClass = field.getDeclaringClass();
                ConverterMatcher converter = Enum.class.isAssignableFrom(type)
                    ? (ConverterMatcher)enumMapper.getConverterFromItemType(null, type, null)
                    : (ConverterMatcher)mapper.getLocalConverter(declaringClass, fieldName);
                if (converter == null) {
                    converter = lookup.lookupConverterForType(type);
                }

                if (!(converter instanceof SingleValueConverter)) {
                    final ConversionException exception = new ConversionException(
                        "Cannot read field as a single value for object");
                    exception.add("field", fieldName);
                    exception.add("type", resultType.getName());
                    throw exception;
                }

                if (converter != null) {
                    final Object value = ((SingleValueConverter)converter).fromString(reader.getAttribute(attrName));
                    if (type.isPrimitive()) {
                        type = Primitives.box(type);
                    }

                    if (value != null && !type.isAssignableFrom(value.getClass())) {
                        final ConversionException exception = new ConversionException("Cannot assign object to type");
                        exception.add("object type", value.getClass().getName());
                        exception.add("target type", type.getName());
                        throw exception;
                    }

                    reflectionProvider.writeField(result, fieldName, value, declaringClass);
                    if (!seenFields.add(declaringClass, fieldName)) {
                        throw new DuplicateFieldException(fieldName + " [" + declaringClass.getName() + "]");
                    }
                }
            }
        }

        if (valueField != null) {
            final Class<?> classDefiningField = valueField.getDeclaringClass();
            final String fieldName = valueField.getName();
            final Field field = fieldName == null ? null : reflectionProvider.getField(classDefiningField, fieldName);
            if (fieldName == null || field == null) {
                final ConversionException exception = new ConversionException("Cannot assign value to field of type");
                exception.add("element", reader.getNodeName());
                exception.add("field", fieldName);
                exception.add("target type", context.getRequiredType().getName());
                throw exception;
            }

            Class<?> type;
            final String classAttribute = HierarchicalStreams.readClassAttribute(reader, mapper);
            if (classAttribute != null) {
                type = mapper.realClass(classAttribute);
            } else {
                type = mapper.defaultImplementationOf(reflectionProvider.getFieldType(result, fieldName,
                    classDefiningField));
            }

            final Object value = context.convertAnother(result, type, mapper.getLocalConverter(field
                .getDeclaringClass(), field.getName()));

            final Class<?> definedType = reflectionProvider.getFieldType(result, fieldName, classDefiningField);
            if (!definedType.isPrimitive()) {
                type = definedType;
            }

            if (value != null && !type.isAssignableFrom(value.getClass())) {
                final ConversionException exception = new ConversionException("Cannot assign object to type");
                exception.add("object type", value.getClass().getName());
                exception.add("target type", type.getName());
                throw exception;
            }

            reflectionProvider.writeField(result, fieldName, value, classDefiningField);
            if (!seenFields.add(classDefiningField, fieldName)) {
                throw new DuplicateFieldException(fieldName + " [" + classDefiningField.getName() + "]");
            }
        }
        return result;
    }

    private boolean fieldIsEqual(final Class<?> definedIn, final String name) {
        return valueField.getName().equals(name)
            && valueField.getDeclaringClass().getName().equals(definedIn.getName());
    }
}
