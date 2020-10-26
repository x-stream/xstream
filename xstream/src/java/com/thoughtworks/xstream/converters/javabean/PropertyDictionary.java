/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.converters.javabean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.reflection.MissingFieldException;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.Caching;


/**
 * Builds the properties maps for each bean and caches them.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class PropertyDictionary implements Caching {
    private transient Map<Class<?>, Map<String, PropertyDescriptor>> propertyNameCache = Collections.synchronizedMap(
        new HashMap<Class<?>, Map<String, PropertyDescriptor>>());
    private final PropertySorter sorter;

    public PropertyDictionary() {
        this(new NativePropertySorter());
    }

    public PropertyDictionary(final PropertySorter sorter) {
        this.sorter = sorter;
    }

    /**
     * @deprecated As of 1.3.1, use {@link #propertiesFor(Class)} instead
     */
    @Deprecated
    public Iterator<BeanProperty> serializablePropertiesFor(final Class<?> type) {
        final Collection<BeanProperty> beanProperties = new ArrayList<>();
        final Collection<PropertyDescriptor> descriptors = buildMap(type).values();
        for (final PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
                beanProperties.add(new BeanProperty(type, descriptor.getName(), descriptor.getPropertyType()));
            }
        }
        return beanProperties.iterator();
    }

    /**
     * Locates a serializable property.
     *
     * @param cls
     * @param name
     * @deprecated As of 1.3.1, use {@link #propertyDescriptor(Class, String)} instead
     */
    @Deprecated
    public BeanProperty property(final Class<?> cls, final String name) {
        BeanProperty beanProperty = null;
        final PropertyDescriptor descriptor = propertyDescriptorOrNull(cls, name);
        if (descriptor == null) {
            throw new MissingFieldException(cls.getName(), name);
        }
        if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
            beanProperty = new BeanProperty(cls, descriptor.getName(), descriptor.getPropertyType());
        }
        return beanProperty;
    }

    public Iterator<PropertyDescriptor> propertiesFor(final Class<?> type) {
        return buildMap(type).values().iterator();
    }

    /**
     * Locates a property descriptor.
     *
     * @param type
     * @param name
     * @throws MissingFieldException if property does not exist
     */
    public PropertyDescriptor propertyDescriptor(final Class<?> type, final String name) {
        final PropertyDescriptor descriptor = propertyDescriptorOrNull(type, name);
        if (descriptor == null) {
            throw new MissingFieldException(type.getName(), name);
        }
        return descriptor;
    }

    /**
     * Locates a property descriptor.
     *
     * @param type
     * @param name
     * @return {@code null} if property does not exist
     * @since 1.4.10
     */
    public PropertyDescriptor propertyDescriptorOrNull(final Class<?> type, final String name) {
        return buildMap(type).get(name);
    }

    private Map<String, PropertyDescriptor> buildMap(final Class<?> type) {
        Map<String, PropertyDescriptor> nameMap = propertyNameCache.get(type);
        if (nameMap == null) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(type, Object.class);
            } catch (final IntrospectionException e) {
                final ObjectAccessException oaex = new ObjectAccessException("Cannot get BeanInfo of type", e);
                oaex.add("bean-type", type.getName());
                throw oaex;
            }
            nameMap = new LinkedHashMap<>();
            final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (final PropertyDescriptor descriptor : propertyDescriptors) {
                nameMap.put(descriptor.getName(), descriptor);
            }
            nameMap = sorter.sort(type, nameMap);
            propertyNameCache.put(type, nameMap);
        }
        return nameMap;
    }

    @Override
    public void flushCache() {
        propertyNameCache.clear();
    }
}
