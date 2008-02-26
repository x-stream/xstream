/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
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


/**
 * Default mapper implementation with 'vanilla' functionality. To build up the functionality required, wrap this mapper
 * with other mapper implementations.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class DefaultMapper implements Mapper {

    private final ClassLoader classLoader;
    /**
     * @deprecated since 1.2, no necessity for field anymore.
     */
    private transient String classAttributeIdentifier;

    public DefaultMapper(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.classAttributeIdentifier = "class";
    }

    /**
     * @deprecated since 1.2, use XStream.aliasAttrbute() for a different attribute name.
     */
    public DefaultMapper(ClassLoader classLoader, String classAttributeIdentifier) {
        this(classLoader);
        this.classAttributeIdentifier = classAttributeIdentifier == null ? "class" : classAttributeIdentifier;
    }

    /**
     * @deprecated since 1.2, no necessity for method anymore.
     */
    private Object readResolve() {
        classAttributeIdentifier = "class";
        return this;
    }

    public String serializedClass(Class type) {
        return type.getName();
    }

    public Class realClass(String elementName) {
        try {
            return classLoader.loadClass(elementName);
        } catch (ClassNotFoundException e) {
            throw new CannotResolveClassException(elementName + " : " + e.getMessage());
        }
    }

    public Class defaultImplementationOf(Class type) {
        return type;
    }

    /**
     * @deprecated since 1.2, use aliasForAttribute instead.
     */
    public String attributeForClassDefiningField() {
        return "defined-in";
    }

    /**
     * @deprecated since 1.2, use aliasForAttribute instead.
     */
    public String attributeForReadResolveField() {
        return "resolves-to";
    }

    /**
     * @deprecated since 1.2, use aliasForAttribute instead.
     */
    public String attributeForEnumType() {
        return "enum-type";
    }

    /**
     * @deprecated since 1.2, use aliasForAttribute instead.
     */
    public String attributeForImplementationClass() {
        return classAttributeIdentifier;
    }

    public String aliasForAttribute(String attribute) {
        return attribute;
    }

    public String attributeForAlias(String alias) {
        return alias;
    }

    public boolean isImmutableValueType(Class type) {
        return false;
    }

    public String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName) {
        return null;
    }

    public Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName) {
        return null;
    }

    public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class itemType, String fieldName) {
        return null;
    }

    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
        return true;
    }

    public String lookupName(Class type) {
        return serializedClass(type);
    }

    public Class lookupType(String elementName) {
        return realClass(elementName);
    }

    public String serializedMember(Class type, String memberName) {
        return memberName;
    }

    public String realMember(Class type, String serialized) {
        return serialized;
    }

    /**
     * @deprecated since 1.3, use {@link #getConverterFromAttribute(Class, String)}
     */
    public SingleValueConverter getConverterFromAttribute(String name) {
        return null;
    }

    /**
     * @deprecated since 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
     */
    public SingleValueConverter getConverterFromItemType(String fieldName, Class type) {
        return null;
    }

    /**
     * @deprecated since 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
     */
    public SingleValueConverter getConverterFromItemType(Class type) {
        return null;
    }

    public SingleValueConverter getConverterFromItemType(String fieldName, Class type,
        Class definedIn) {
        return null;
    }

    public Converter getLocalConverter(Class definedIn, String fieldName) {
        return null;
    }

    public Mapper lookupMapperOfType(Class type) {
        return null;
    }

    /**
     * @deprecated since 1.3, use combination of {@link #serializedMember(Class, String)} and {@link #getConverterFromItemType(String, Class, Class)} 
     */
    public String aliasForAttribute(Class definedIn, String fieldName) {
        return fieldName;
    }

    /**
     * @deprecated since 1.3, use combination of {@link #realMember(Class, String)} and {@link #getConverterFromItemType(String, Class, Class)} 
     */
    public String attributeForAlias(Class definedIn, String alias) {
        return alias;
    }

    public SingleValueConverter getConverterFromAttribute(Class type, String attribute) {
        return null;
    }
}
