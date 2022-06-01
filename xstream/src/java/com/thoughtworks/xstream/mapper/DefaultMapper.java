/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2013, 2014, 2015, 2016, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 22. January 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.util.Primitives;


/**
 * Default mapper implementation with 'vanilla' functionality.
 * <p>
 * To build up the functionality required, wrap this mapper with other mapper implementations.
 * </p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class DefaultMapper implements Mapper {

    private static String XSTREAM_PACKAGE_ROOT;
    static {
        final String packageName = DefaultMapper.class.getName();
        final int idx = packageName.indexOf(".xstream.");
        XSTREAM_PACKAGE_ROOT = idx > 0 ? packageName.substring(0, idx + 9) : ".N/A";
    }

    private final ClassLoaderReference classLoaderReference;

    /**
     * Construct a DefaultMapper.
     *
     * @param classLoaderReference the reference to the classloader used by the XStream instance.
     * @since 1.4.5
     */
    public DefaultMapper(final ClassLoaderReference classLoaderReference) {
        this.classLoaderReference = classLoaderReference;
    }

    /**
     * Construct a DefaultMapper.
     *
     * @param classLoader the ClassLoader used by the XStream instance.
     * @deprecated As of 1.4.5 use {@link #DefaultMapper(ClassLoaderReference)}
     */
    @Deprecated
    public DefaultMapper(final ClassLoader classLoader) {
        this(new ClassLoaderReference(classLoader));
    }

    @Override
    public String serializedClass(final Class<?> type) {
        return type.getName();
    }

    @Override
    public Class<?> realClass(final String elementName) {
        final Class<?> resultingClass = Primitives.primitiveType(elementName);
        if (resultingClass != null) {
            return resultingClass;
        }
        try {
            boolean initialize = true;
            final ClassLoader classLoader;
            if (elementName.startsWith(XSTREAM_PACKAGE_ROOT)) {
                classLoader = DefaultMapper.class.getClassLoader();
            } else {
                classLoader = classLoaderReference.getReference();
                initialize = elementName.charAt(0) == '[';
            }
            return Class.forName(elementName, initialize, classLoader);
        } catch (final ClassNotFoundException | IllegalArgumentException e) {
            throw new CannotResolveClassException(elementName);
        }
    }

    @Override
    public Class<?> defaultImplementationOf(final Class<?> type) {
        return type;
    }

    @Override
    public String aliasForAttribute(final String attribute) {
        return attribute;
    }

    @Override
    public String attributeForAlias(final String alias) {
        return alias;
    }

    @Override
    public String aliasForSystemAttribute(final String attribute) {
        return attribute;
    }

    @Override
    public boolean isImmutableValueType(final Class<?> type) {
        return false;
    }

    @Override
    public boolean isReferenceable(final Class<?> type) {
        return true;
    }

    @Override
    public String getFieldNameForItemTypeAndName(final Class<?> definedIn, final Class<?> itemType,
            final String itemFieldName) {
        return null;
    }

    @Override
    public Class<?> getItemTypeForItemFieldName(final Class<?> definedIn, final String itemFieldName) {
        return null;
    }

    @Override
    public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(final Class<?> itemType,
            final String fieldName) {
        return null;
    }

    @Override
    public boolean shouldSerializeMember(final Class<?> definedIn, final String fieldName) {
        return true;
    }

    @Override
    public boolean isIgnoredElement(final String name) {
        return false;
    }

    public String lookupName(final Class<?> type) {
        return serializedClass(type);
    }

    public Class<?> lookupType(final String elementName) {
        return realClass(elementName);
    }

    @Override
    public String serializedMember(final Class<?> type, final String memberName) {
        return memberName;
    }

    @Override
    public String realMember(final Class<?> type, final String serialized) {
        return serialized;
    }

    /**
     * @deprecated As of 1.3, use {@link #getConverterFromAttribute(Class, String, Class)}
     */
    @Deprecated
    public SingleValueConverter getConverterFromAttribute(final String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
     */
    @Deprecated
    public SingleValueConverter getConverterFromItemType(final String fieldName, final Class<?> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
     */
    @Deprecated
    public SingleValueConverter getConverterFromItemType(final Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SingleValueConverter getConverterFromItemType(final String fieldName, final Class<?> type,
            final Class<?> definedIn) {
        return null;
    }

    @Override
    public Converter getLocalConverter(final Class<?> definedIn, final String fieldName) {
        return null;
    }

    @Override
    public <T extends Mapper> T lookupMapperOfType(final Class<T> type) {
        @SuppressWarnings("unchecked")
        final T t = type.isAssignableFrom(getClass()) ? (T)this : null;
        return t;
    }

    /**
     * @deprecated As of 1.3, use combination of {@link #serializedMember(Class, String)} and
     *             {@link #getConverterFromItemType(String, Class, Class)}
     */
    @Deprecated
    public String aliasForAttribute(final Class<?> definedIn, final String fieldName) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated As of 1.3, use combination of {@link #realMember(Class, String)} and
     *             {@link #getConverterFromItemType(String, Class, Class)}
     */
    @Deprecated
    public String attributeForAlias(final Class<?> definedIn, final String alias) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated As of 1.3.1, use {@link #getConverterFromAttribute(Class, String, Class)}
     */
    @Deprecated
    @Override
    public SingleValueConverter getConverterFromAttribute(final Class<?> definedIn, final String attribute) {
        return null;
    }

    @Override
    public SingleValueConverter getConverterFromAttribute(final Class<?> definedIn, final String attribute,
            final Class<?> type) {
        return null;
    }
}
