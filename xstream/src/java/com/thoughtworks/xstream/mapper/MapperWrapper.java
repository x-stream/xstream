/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2013, 2014, 2015 XStream Committers.
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


public abstract class MapperWrapper implements Mapper {

    private final Mapper wrapped;

    public MapperWrapper(final Mapper wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String serializedClass(final Class<?> type) {
        return wrapped.serializedClass(type);
    }

    @Override
    public Class<?> realClass(final String elementName) {
        return wrapped.realClass(elementName);
    }

    @Override
    public String serializedMember(final Class<?> type, final String memberName) {
        return wrapped.serializedMember(type, memberName);
    }

    @Override
    public String realMember(final Class<?> type, final String serialized) {
        return wrapped.realMember(type, serialized);
    }

    @Override
    public boolean isImmutableValueType(final Class<?> type) {
        return wrapped.isImmutableValueType(type);
    }

    @Override
    public boolean isReferenceable(Class<?> type) {
        return wrapped.isReferenceable(type);
    }

    @Override
    public Class<?> defaultImplementationOf(final Class<?> type) {
        return wrapped.defaultImplementationOf(type);
    }

    @Override
    public String aliasForAttribute(final String attribute) {
        return wrapped.aliasForAttribute(attribute);
    }

    @Override
    public String attributeForAlias(final String alias) {
        return wrapped.attributeForAlias(alias);
    }

    @Override
    public String aliasForSystemAttribute(final String attribute) {
        return wrapped.aliasForSystemAttribute(attribute);
    }

    @Override
    public String getFieldNameForItemTypeAndName(final Class<?> definedIn, final Class<?> itemType,
            final String itemFieldName) {
        return wrapped.getFieldNameForItemTypeAndName(definedIn, itemType, itemFieldName);
    }

    @Override
    public Class<?> getItemTypeForItemFieldName(final Class<?> definedIn, final String itemFieldName) {
        return wrapped.getItemTypeForItemFieldName(definedIn, itemFieldName);
    }

    @Override
    public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(final Class<?> itemType,
            final String fieldName) {
        return wrapped.getImplicitCollectionDefForFieldName(itemType, fieldName);
    }

    @Override
    public boolean shouldSerializeMember(final Class<?> definedIn, final String fieldName) {
        return wrapped.shouldSerializeMember(definedIn, fieldName);
    }

    @Override
    public Converter getLocalConverter(final Class<?> definedIn, final String fieldName) {
        return wrapped.getLocalConverter(definedIn, fieldName);
    }

    @Override
    public <T extends Mapper> T lookupMapperOfType(final Class<T> type) {
        @SuppressWarnings("unchecked")
        final T t = type.isAssignableFrom(getClass()) ? (T)this : wrapped.lookupMapperOfType(type);
        return t;
    }

    @Override
    public SingleValueConverter getConverterFromItemType(final String fieldName, final Class<?> type,
            final Class<?> definedIn) {
        return wrapped.getConverterFromItemType(fieldName, type, definedIn);
    }

    /**
     * @deprecated As of 1.3.1, use {@link #getConverterFromAttribute(Class, String, Class)}
     */
    @Deprecated
    @Override
    public SingleValueConverter getConverterFromAttribute(final Class<?> type, final String attribute) {
        return wrapped.getConverterFromAttribute(type, attribute);
    }

    @Override
    public SingleValueConverter getConverterFromAttribute(final Class<?> definedIn, final String attribute,
            final Class<?> type) {
        return wrapped.getConverterFromAttribute(definedIn, attribute, type);
    }

}
