/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2018, 2024 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.javabean;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.MissingFieldException;
import com.thoughtworks.xstream.core.util.MemberDictionary;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Can convert any bean with a public default constructor. The {@link BeanProvider} used as default is based on
 * {@link java.beans.BeanInfo}. Indexed properties are currently not supported.
 */
public class JavaBeanConverter implements Converter {

    /* TODO: - support indexed properties - support attributes (XSTR-620) - support local converters (XSTR-601) Problem:
     * Mappers take definitions based on reflection, they don't know about bean info */
    protected final Mapper mapper;
    protected final JavaBeanProvider beanProvider;
    private final Class<?> type;

    public JavaBeanConverter(final Mapper mapper) {
        this(mapper, (Class<?>)null);
    }

    public JavaBeanConverter(final Mapper mapper, final Class<?> type) {
        this(mapper, new BeanProvider(), type);
    }

    public JavaBeanConverter(final Mapper mapper, final JavaBeanProvider beanProvider) {
        this(mapper, beanProvider, null);
    }

    public JavaBeanConverter(final Mapper mapper, final JavaBeanProvider beanProvider, final Class<?> type) {
        this.mapper = mapper;
        this.beanProvider = beanProvider;
        this.type = type;
    }

    /**
     * Checks if the bean provider can instantiate this type. If you need less strict checks, subclass JavaBeanConverter
     */
    @Override
    public boolean canConvert(final Class<?> type) {
        return (this.type == null || this.type == type) && beanProvider.canInstantiate(type);
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final String classAttributeName = mapper.aliasForSystemAttribute("class");
        beanProvider.visitSerializableProperties(source, new JavaBeanProvider.Visitor() {
            @Override
            public boolean shouldVisit(final String name, final Class<?> definedIn) {
                return mapper.shouldSerializeMember(definedIn, name);
            }

            @Override
            public void visit(final String propertyName, final Class<?> fieldType, final Class<?> definedIn,
                    final Object newObj) {
                if (newObj != null) {
                    writeField(propertyName, fieldType, newObj);
                } else {
                    writeNullField(propertyName);
                }
            }

            private void writeField(final String propertyName, final Class<?> fieldType, final Object newObj) {
                final Class<?> actualType = newObj.getClass();
                final Class<?> defaultType = mapper.defaultImplementationOf(fieldType);
                final String serializedMember = mapper.serializedMember(source.getClass(), propertyName);
                writer.startNode(serializedMember, actualType);
                if (!actualType.equals(defaultType) && classAttributeName != null) {
                    writer.addAttribute(classAttributeName, mapper.serializedClass(actualType));
                }
                context.convertAnother(newObj);

                writer.endNode();
            }

            private void writeNullField(final String propertyName) {
                final String serializedMember = mapper.serializedMember(source.getClass(), propertyName);
                writer.startNode(serializedMember, Mapper.Null.class);
                writer.addAttribute(classAttributeName, mapper.serializedClass(Mapper.Null.class));
                writer.endNode();
            }
        });
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Object result = instantiateNewInstance(context);
        final MemberDictionary seenProperties = new MemberDictionary();

        final Class<?> resultType = result.getClass();
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            final String propertyName = mapper.realMember(resultType, reader.getNodeName());

            if (mapper.shouldSerializeMember(resultType, propertyName)) {
                final boolean propertyExistsInClass = beanProvider.propertyDefinedInClass(propertyName, resultType);

                if (propertyExistsInClass) {
                    final Class<?> type = determineType(reader, result, propertyName);
                    final Object value = context.convertAnother(result, type);
                    beanProvider.writeProperty(result, propertyName, value);
                    if (!seenProperties.add(resultType, propertyName)) {
                        throw new DuplicatePropertyException(propertyName);
                    }
                } else if (!mapper.isIgnoredElement(propertyName)) {
                    throw new MissingFieldException(resultType.getName(), propertyName);
                }
            }
            reader.moveUp();
        }

        return result;
    }

    private Object instantiateNewInstance(final UnmarshallingContext context) {
        Object result = context.currentObject();
        if (result == null) {
            result = beanProvider.newInstance(context.getRequiredType());
        }
        return result;
    }

    private Class<?> determineType(final HierarchicalStreamReader reader, final Object result, final String fieldName) {
        final String classAttributeName = mapper.aliasForSystemAttribute("class");
        final String classAttribute = classAttributeName == null ? null : reader.getAttribute(classAttributeName);
        if (classAttribute != null) {
            return mapper.realClass(classAttribute);
        } else {
            return mapper.defaultImplementationOf(beanProvider.getPropertyType(result, fieldName));
        }
    }

    /**
     * Exception to indicate double processing of a property to avoid silent clobbering.
     *
     * @author J&ouml;rg Schaible
     * @since 1.4.2
     */
    public static class DuplicatePropertyException extends ConversionException {
        private static final long serialVersionUID = 10402L;

        public DuplicatePropertyException(final String msg) {
            super("Duplicate property " + msg);
            add("property", msg);
        }
    }
}
