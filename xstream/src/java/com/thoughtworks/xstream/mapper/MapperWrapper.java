/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2015, 2016 XStream Committers.
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

    public MapperWrapper(Mapper wrapped) {
        this.wrapped = wrapped;

        if (wrapped instanceof MapperWrapper) {
            final MapperWrapper wrapper = (MapperWrapper)wrapped;
            final Map wrapperMap = new HashMap();
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
            for (int i = 0; i < methods.length; ++i) {
                final Method method = methods[i];
                if (method.getDeclaringClass() != MapperWrapper.class) {
                    final String name = method.getName();
                    if (wrapperMap.containsKey(name)) {
                        wrapperMap.put(name, wrapped);
                    }
                }
            }

            aliasForAttributeMapper = (Mapper)wrapperMap.get("aliasForAttribute");
            aliasForSystemAttributeMapper = (Mapper)wrapperMap.get("aliasForSystemAttribute");
            attributeForAliasMapper = (Mapper)wrapperMap.get("attributeForAlias");
            defaultImplementationOfMapper = (Mapper)wrapperMap.get("defaultImplementationOf");
            getConverterFromAttributeMapper = (Mapper)wrapperMap.get("getConverterFromAttribute");
            getConverterFromItemTypeMapper = (Mapper)wrapperMap.get("getConverterFromItemType");
            getFieldNameForItemTypeAndNameMapper = (Mapper)wrapperMap.get("getFieldNameForItemTypeAndName");
            getImplicitCollectionDefForFieldNameMapper = (Mapper)wrapperMap.get("getImplicitCollectionDefForFieldName");
            getItemTypeForItemFieldNameMapper = (Mapper)wrapperMap.get("getItemTypeForItemFieldName");
            getLocalConverterMapper = (Mapper)wrapperMap.get("getLocalConverter");
            isIgnoredElementMapper = (Mapper)wrapperMap.get("isIgnoredElement");
            isImmutableValueTypeMapper = (Mapper)wrapperMap.get("isImmutableValueType");
            isReferenceableMapper = (Mapper)wrapperMap.get("isReferenceable");
            realClassMapper = (Mapper)wrapperMap.get("realClass");
            realMemberMapper = (Mapper)wrapperMap.get("realMember");
            serializedClassMapper = (Mapper)wrapperMap.get("serializedClass");
            serializedMemberMapper = (Mapper)wrapperMap.get("serializedMember");
            shouldSerializeMemberMapper = (Mapper)wrapperMap.get("shouldSerializeMember");
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

    public String serializedClass(Class type) {
        return serializedClassMapper.serializedClass(type);
    }

    public Class realClass(String elementName) {
        return realClassMapper.realClass(elementName);
    }

    public String serializedMember(Class type, String memberName) {
        return serializedMemberMapper.serializedMember(type, memberName);
    }

    public String realMember(Class type, String serialized) {
        return realMemberMapper.realMember(type, serialized);
    }

    public boolean isImmutableValueType(Class type) {
        return isImmutableValueTypeMapper.isImmutableValueType(type);
    }

    public boolean isReferenceable(Class type) {
        return isReferenceableMapper.isReferenceable(type);
    }

    public Class defaultImplementationOf(Class type) {
        return defaultImplementationOfMapper.defaultImplementationOf(type);
    }

    public String aliasForAttribute(String attribute) {
        return aliasForAttributeMapper.aliasForAttribute(attribute);
    }

    public String attributeForAlias(String alias) {
        return attributeForAliasMapper.attributeForAlias(alias);
    }

    public String aliasForSystemAttribute(String attribute) {
        return aliasForSystemAttributeMapper.aliasForSystemAttribute(attribute);
    }

    public String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName) {
        return getFieldNameForItemTypeAndNameMapper.getFieldNameForItemTypeAndName(definedIn, itemType, itemFieldName);
    }

    public Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName) {
        return getItemTypeForItemFieldNameMapper.getItemTypeForItemFieldName(definedIn, itemFieldName);
    }

    public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class itemType, String fieldName) {
        return getImplicitCollectionDefForFieldNameMapper.getImplicitCollectionDefForFieldName(itemType, fieldName);
    }

    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
        return shouldSerializeMemberMapper.shouldSerializeMember(definedIn, fieldName);
    }
    
    public boolean isIgnoredElement(String name) {
        return isIgnoredElementMapper.isIgnoredElement(name);
    }

    /**
     * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
     */
    public SingleValueConverter getConverterFromItemType(String fieldName, Class type) {
        return getConverterFromItemTypeMapper.getConverterFromItemType(fieldName, type);
    }

    /**
     * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
     */
    public SingleValueConverter getConverterFromItemType(Class type) {
        return getConverterFromItemTypeMapper.getConverterFromItemType(type);
    }

    /**
     * @deprecated As of 1.3, use {@link #getConverterFromAttribute(Class, String, Class)}
     */
    public SingleValueConverter getConverterFromAttribute(String name) {
        return getConverterFromAttributeMapper.getConverterFromAttribute(name);
    }

    public Converter getLocalConverter(Class definedIn, String fieldName) {
        return getLocalConverterMapper.getLocalConverter(definedIn, fieldName);
    }

    public Mapper lookupMapperOfType(Class type) {
        return type.isAssignableFrom(getClass()) ? this : wrapped.lookupMapperOfType(type);
    }

    public SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn) {
        return getConverterFromItemTypeMapper.getConverterFromItemType(fieldName, type, definedIn);
    }

    /**
     * @deprecated As of 1.3, use combination of {@link #serializedMember(Class, String)} and
     *             {@link #getConverterFromItemType(String, Class, Class)}
     */
    public String aliasForAttribute(Class definedIn, String fieldName) {
        return aliasForAttributeMapper.aliasForAttribute(definedIn, fieldName);
    }

    /**
     * @deprecated As of 1.3, use combination of {@link #realMember(Class, String)} and
     *             {@link #getConverterFromItemType(String, Class, Class)}
     */
    public String attributeForAlias(Class definedIn, String alias) {
        return attributeForAliasMapper.attributeForAlias(definedIn, alias);
    }

    /**
     * @deprecated As of 1.3.1, use {@link #getConverterFromAttribute(Class, String, Class)}
     */
    public SingleValueConverter getConverterFromAttribute(Class type, String attribute) {
        return getConverterFromAttributeMapper.getConverterFromAttribute(type, attribute);
    }

    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
        return getConverterFromAttributeMapper.getConverterFromAttribute(definedIn, attribute, type);
    }

}
