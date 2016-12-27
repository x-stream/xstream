/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2013, 2014, 2015, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 22. January 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;


public abstract class MapperWrapper implements Mapper {
    private final Mapper wrapped;
    private final Mapper aliasForAttributeMapper;
    private final Mapper aliasForSystemAttributeMapper;
    private final Mapper attributeForAliasMapper;
    private final Mapper defaultImplementationOfMapper;
    private final Mapper getConverterFromAttributeMapper;
    private final Mapper getConverterFromItemTypeMapper;
    private final Mapper getFieldNameForItemTypeAndNameMapper;
    private final Mapper getImplicitCollectionDefForFieldNameMapper;
    private final Mapper getItemTypeForItemFieldNameMapper;
    private final Mapper getLocalConverterMapper;
    private final Mapper isIgnoredElementMapper;
    private final Mapper isImmutableValueTypeMapper;
    private final Mapper isReferenceableMapper;
    private final Mapper realClassMapper;
    private final Mapper realMemberMapper;
    private final Mapper serializedClassMapper;
    private final Mapper serializedMemberMapper;
    private final Mapper shouldSerializeMemberMapper;

    public MapperWrapper(final Mapper wrapped) {
        this.wrapped = wrapped;

        if (wrapped instanceof MapperWrapper) {
            final MapperWrapper wrapper = (MapperWrapper)wrapped;
            final Map<String, Mapper> wrapperMap = new HashMap<>();
            wrapperMap.put("aliasForAttribute", wrapper.aliasForAttributeMapper);
            wrapperMap.put("aliasForSystemAttribute", wrapper.aliasForSystemAttributeMapper);
            wrapperMap.put("attributeForAlias", wrapper.attributeForAliasMapper);
            wrapperMap.put("defaultImplementationOf", wrapper.defaultImplementationOfMapper);
            wrapperMap.put("getConverterFromAttribute", wrapper.getConverterFromAttributeMapper);
            wrapperMap.put("getConverterFromItemType", wrapper.getConverterFromItemTypeMapper);
            wrapperMap.put("getFieldNameForItemTypeAndName", wrapper.getFieldNameForItemTypeAndNameMapper);
            wrapperMap.put("getImplicitCollectionDefForFieldName", wrapper.getImplicitCollectionDefForFieldNameMapper);
            wrapperMap.put("getItemTypeForItemFieldName", wrapper.getItemTypeForItemFieldNameMapper);
            wrapperMap.put("getLocalConverter", wrapper.getLocalConverterMapper);
            wrapperMap.put("isIgnoredElement", wrapper.isIgnoredElementMapper);
            wrapperMap.put("isImmutableValueType", wrapper.isImmutableValueTypeMapper);
            wrapperMap.put("isReferenceable", wrapper.isReferenceableMapper);
            wrapperMap.put("realClass", wrapper.realClassMapper);
            wrapperMap.put("realMember", wrapper.realMemberMapper);
            wrapperMap.put("serializedClass", wrapper.serializedClassMapper);
            wrapperMap.put("serializedMember", wrapper.serializedMemberMapper);
            wrapperMap.put("shouldSerializeMember", wrapper.shouldSerializeMemberMapper);

            final Method[] methods = wrapped.getClass().getMethods();
            for (final Method method : methods) {
                if (method.getDeclaringClass() != MapperWrapper.class) {
                    final String name = method.getName();
                    if (wrapperMap.containsKey(name)) {
                        wrapperMap.put(name, wrapped);
                    }
                }
            }

            aliasForAttributeMapper = wrapperMap.get("aliasForAttribute");
            aliasForSystemAttributeMapper = wrapperMap.get("aliasForSystemAttribute");
            attributeForAliasMapper = wrapperMap.get("attributeForAlias");
            defaultImplementationOfMapper = wrapperMap.get("defaultImplementationOf");
            getConverterFromAttributeMapper = wrapperMap.get("getConverterFromAttribute");
            getConverterFromItemTypeMapper = wrapperMap.get("getConverterFromItemType");
            getFieldNameForItemTypeAndNameMapper = wrapperMap.get("getFieldNameForItemTypeAndName");
            getImplicitCollectionDefForFieldNameMapper = wrapperMap.get("getImplicitCollectionDefForFieldName");
            getItemTypeForItemFieldNameMapper = wrapperMap.get("getItemTypeForItemFieldName");
            getLocalConverterMapper = wrapperMap.get("getLocalConverter");
            isIgnoredElementMapper = wrapperMap.get("isIgnoredElement");
            isImmutableValueTypeMapper = wrapperMap.get("isImmutableValueType");
            isReferenceableMapper = wrapperMap.get("isReferenceable");
            realClassMapper = wrapperMap.get("realClass");
            realMemberMapper = wrapperMap.get("realMember");
            serializedClassMapper = wrapperMap.get("serializedClass");
            serializedMemberMapper = wrapperMap.get("serializedMember");
            shouldSerializeMemberMapper = wrapperMap.get("shouldSerializeMember");
        } else {
            aliasForAttributeMapper = wrapped;
            aliasForSystemAttributeMapper = wrapped;
            attributeForAliasMapper = wrapped;
            defaultImplementationOfMapper = wrapped;
            getConverterFromAttributeMapper = wrapped;
            getConverterFromItemTypeMapper = wrapped;
            getFieldNameForItemTypeAndNameMapper = wrapped;
            getImplicitCollectionDefForFieldNameMapper = wrapped;
            getItemTypeForItemFieldNameMapper = wrapped;
            getLocalConverterMapper = wrapped;
            isIgnoredElementMapper = wrapped;
            isImmutableValueTypeMapper = wrapped;
            isReferenceableMapper = wrapped;
            realClassMapper = wrapped;
            realMemberMapper = wrapped;
            serializedClassMapper = wrapped;
            serializedMemberMapper = wrapped;
            shouldSerializeMemberMapper = wrapped;
        }

    }

    @Override
    public String serializedClass(final Class<?> type) {
        return serializedClassMapper.serializedClass(type);
    }

    @Override
    public Class<?> realClass(final String elementName) {
        return realClassMapper.realClass(elementName);
    }

    @Override
    public String serializedMember(final Class<?> type, final String memberName) {
        return serializedMemberMapper.serializedMember(type, memberName);
    }

    @Override
    public String realMember(final Class<?> type, final String serialized) {
        return realMemberMapper.realMember(type, serialized);
    }

    @Override
    public boolean isImmutableValueType(final Class<?> type) {
        return isImmutableValueTypeMapper.isImmutableValueType(type);
    }

    @Override
    public boolean isReferenceable(final Class<?> type) {
        return isReferenceableMapper.isReferenceable(type);
    }

    @Override
    public Class<?> defaultImplementationOf(final Class<?> type) {
        return defaultImplementationOfMapper.defaultImplementationOf(type);
    }

    @Override
    public String aliasForAttribute(final String attribute) {
        return aliasForAttributeMapper.aliasForAttribute(attribute);
    }

    @Override
    public String attributeForAlias(final String alias) {
        return attributeForAliasMapper.attributeForAlias(alias);
    }

    @Override
    public String aliasForSystemAttribute(final String attribute) {
        return aliasForSystemAttributeMapper.aliasForSystemAttribute(attribute);
    }

    @Override
    public String getFieldNameForItemTypeAndName(final Class<?> definedIn, final Class<?> itemType,
            final String itemFieldName) {
        return getFieldNameForItemTypeAndNameMapper.getFieldNameForItemTypeAndName(definedIn, itemType, itemFieldName);
    }

    @Override
    public Class<?> getItemTypeForItemFieldName(final Class<?> definedIn, final String itemFieldName) {
        return getItemTypeForItemFieldNameMapper.getItemTypeForItemFieldName(definedIn, itemFieldName);
    }

    @Override
    public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(final Class<?> itemType,
            final String fieldName) {
        return getImplicitCollectionDefForFieldNameMapper.getImplicitCollectionDefForFieldName(itemType, fieldName);
    }

    @Override
    public boolean shouldSerializeMember(final Class<?> definedIn, final String fieldName) {
        return shouldSerializeMemberMapper.shouldSerializeMember(definedIn, fieldName);
    }

    @Override
    public boolean isIgnoredElement(final String name) {
        return isIgnoredElementMapper.isIgnoredElement(name);
    }

    @Override
    public Converter getLocalConverter(final Class<?> definedIn, final String fieldName) {
        return getLocalConverterMapper.getLocalConverter(definedIn, fieldName);
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
        return getConverterFromItemTypeMapper.getConverterFromItemType(fieldName, type, definedIn);
    }

    /**
     * @deprecated As of 1.3.1, use {@link #getConverterFromAttribute(Class, String, Class)}
     */
    @Deprecated
    @Override
    public SingleValueConverter getConverterFromAttribute(final Class<?> type, final String attribute) {
        return getConverterFromAttributeMapper.getConverterFromAttribute(type, attribute);
    }

    @Override
    public SingleValueConverter getConverterFromAttribute(final Class<?> definedIn, final String attribute,
            final Class<?> type) {
        return getConverterFromAttributeMapper.getConverterFromAttribute(definedIn, attribute, type);
    }

}
