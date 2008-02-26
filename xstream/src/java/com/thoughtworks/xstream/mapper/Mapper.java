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

public interface Mapper {
    /**
     * Place holder type used for null values.
     */
    class Null {}

    /**
     * How a class name should be represented in its serialized form.
     */
    String serializedClass(Class type);

    /**
     * How a serialized class representation should be mapped back to a real class.
     */
    Class realClass(String elementName);

    /**
     * How a class member should be represented in its serialized form.
     */
    String serializedMember(Class type, String memberName);

    /**
     * How a serialized member representation should be mapped back to a real member.
     */
    String realMember(Class type, String serialized);

    /**
     * Whether this type is a simple immutable value (int, boolean, String, URL, etc.
     * Immutable types will be repeatedly written in the serialized stream, instead of using object references.
     */
    boolean isImmutableValueType(Class type);

    Class defaultImplementationOf(Class type);

    /**
     * @deprecated since 1.2, use aliasForAttribute instead.
     */
    String attributeForImplementationClass();

    /**
     * @deprecated since 1.2, use aliasForAttribute instead.
     */
    String attributeForClassDefiningField();

    /**
     * @deprecated since 1.2, use aliasForAttribute instead.
     */
    String attributeForReadResolveField();

    /**
     * @deprecated since 1.2, use aliasForAttribute instead.
     */
    String attributeForEnumType();

    /**
     * Get the alias for an attrbute's name.
     * 
     * @param attribute the attribute
     * @return the alias
     * @since 1.2
     */
    String aliasForAttribute(String attribute);

    /**
     * Get the attribut's name for an alias.
     * 
     * @param alias the alias
     * @return the attribute's name
     * @since 1.2
     */
    String attributeForAlias(String alias);

    /**
     * Get the name of the field that acts as the default collection for an object, or return null if there is none.
     *
     * @param definedIn     owning type
     * @param itemType      item type
     * @param itemFieldName optional item element name
     */
    String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName);

    Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName);

    ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class itemType, String fieldName);

    /**
     * Determine whether a specific member should be serialized.
     *
     * @since 1.1.3
     */
    boolean shouldSerializeMember(Class definedIn, String fieldName);

    interface ImplicitCollectionMapping {
        String getFieldName();
        String getItemFieldName();
        Class getItemType();
    }

    /**
     * @deprecated since 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
     */
    SingleValueConverter getConverterFromItemType(String fieldName, Class type);

    /**
     * @deprecated since 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
     */
    SingleValueConverter getConverterFromItemType(Class type);

    /**
     * @deprecated since 1.3, use {@link #getConverterFromAttribute(Class, String)}
     */
    SingleValueConverter getConverterFromAttribute(String name);

    Converter getLocalConverter(Class definedIn, String fieldName);

    Mapper lookupMapperOfType(Class type);

    /**
     * Returns a single value converter to be used in a specific field.
     * 
     * @param fieldName the field name
     * @param type the field type
     * @param definedIn the type which defines this field
     * @return a SingleValueConverter or null if there no such converter should be used for this
     *         field.
     * @since 1.2.2
     */
    SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn);

    /**
     * Returns an alias for a single field defined in an specific type.
     * 
     * @param definedIn the type where the field was defined
     * @param fieldName the field name
     * @return the alias for this field or its own name if no alias was defined
     * @since 1.2.2
     * @deprecated since 1.3, use combination of {@link #serializedMember(Class, String)} and {@link #getConverterFromItemType(String, Class, Class)} 
     */
    String aliasForAttribute(Class definedIn, String fieldName);

    /**
     * Returns the field name for an aliased attribute.
     * 
     * @param definedIn the type where the field was defined
     * @param alias the alias
     * @return the original attribute name
     * @since 1.2.2
     * @deprecated since 1.3, use combination of {@link #realMember(Class, String)} and {@link #getConverterFromItemType(String, Class, Class)} 
     */
    String attributeForAlias(Class definedIn, String alias);

    /**
     * Returns which converter to use for an specific attribute in a type.
     * 
     * @param definedIn the field's parent
     * @param attribute the attribute name
     * @since 1.2.2
     */
    SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute);
}
