/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011 XStream Committers.
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

    private static String XSTREAM_PACKAGE_ROOT;
    static {
        String packageName = DefaultMapper.class.getName();
        int idx = packageName.indexOf(".xstream.");
        XSTREAM_PACKAGE_ROOT = idx > 0 ? packageName.substring(0, idx+9) : null;
    }
    
    private final ClassLoader classLoader;

    public DefaultMapper(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String serializedClass(Class type) {
        return type.getName();
    }

    public Class realClass(String elementName) {
        try {
            if (elementName.startsWith(XSTREAM_PACKAGE_ROOT)) {
                return DefaultMapper.class.getClassLoader().loadClass(elementName);
            } else if (elementName.charAt(0) != '[') {
                return classLoader.loadClass(elementName);
            } else if (elementName.endsWith(";")) {
                return Class.forName(elementName.toString(), false, classLoader);
            } else { 
                return Class.forName(elementName.toString());
            }
        } catch (ClassNotFoundException e) {
            throw new CannotResolveClassException(elementName);
        }
    }

    public Class defaultImplementationOf(Class type) {
        return type;
    }

    public String aliasForAttribute(String attribute) {
        return attribute;
    }

    public String attributeForAlias(String alias) {
        return alias;
    }

    public String aliasForSystemAttribute(String attribute) {
        return attribute;
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
     * @deprecated As of 1.3, use {@link #getConverterFromAttribute(Class, String, Class)}
     */
    public SingleValueConverter getConverterFromAttribute(String name) {
        return null;
    }

    /**
     * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
     */
    public SingleValueConverter getConverterFromItemType(String fieldName, Class type) {
        return null;
    }

    /**
     * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
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
     * @deprecated As of 1.3, use combination of {@link #serializedMember(Class, String)} and {@link #getConverterFromItemType(String, Class, Class)} 
     */
    public String aliasForAttribute(Class definedIn, String fieldName) {
        return fieldName;
    }

    /**
     * @deprecated As of 1.3, use combination of {@link #realMember(Class, String)} and {@link #getConverterFromItemType(String, Class, Class)} 
     */
    public String attributeForAlias(Class definedIn, String alias) {
        return alias;
    }

    /**
     * @deprecated As of 1.3.1, use {@link #getConverterFromAttribute(Class, String, Class)} 
     */
    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute) {
        return null;
    }

    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
        return null;
    }
}
