/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 12. April 2005 by Joe Walnes
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
import java.util.Map;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;


/**
 * Builds the properties maps for each bean and caches them.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class PropertyDictionary implements Caching {
    private transient Map propertyNameCache = Collections.synchronizedMap(new HashMap());
    private final PropertySorter sorter;

    public PropertyDictionary() {
        this(new NativePropertySorter());
    }

    public PropertyDictionary(PropertySorter sorter) {
        this.sorter = sorter;
    }

    /**
     * @deprecated As of 1.3.1, use {@link #propertiesFor(Class)} instead
     */
    public Iterator serializablePropertiesFor(Class type) {
        Collection beanProperties = new ArrayList();
        Collection descriptors = buildMap(type).values();
        for (Iterator iter = descriptors.iterator(); iter.hasNext();) {
            PropertyDescriptor descriptor = (PropertyDescriptor)iter.next();
            if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
                beanProperties.add(new BeanProperty(type, descriptor.getName(), descriptor
                    .getPropertyType()));
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
    public BeanProperty property(Class cls, String name) {
        BeanProperty beanProperty = null;
        PropertyDescriptor descriptor = (PropertyDescriptor)buildMap(cls).get(name);
        if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
            beanProperty = new BeanProperty(
                cls, descriptor.getName(), descriptor.getPropertyType());
        }
        return beanProperty;
    }

    public Iterator propertiesFor(Class type) {
        return buildMap(type).values().iterator();
    }

    /**
     * Locates a property descriptor.
     * 
     * @param type
     * @param name
     */
    public PropertyDescriptor propertyDescriptor(Class type, String name) {
        return (PropertyDescriptor)buildMap(type).get(name);
    }

    private Map buildMap(Class type) {
        Map nameMap = (Map)propertyNameCache.get(type);
        if (nameMap == null) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(type, Object.class);
            } catch (IntrospectionException e) {
                throw new ObjectAccessException(
                    "Cannot get BeanInfo of type " + type.getName(), e);
            }
            nameMap = new OrderRetainingMap();
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++ ) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                nameMap.put(descriptor.getName(), descriptor);
            }
            nameMap = sorter.sort(type, nameMap);
            propertyNameCache.put(type, nameMap);
        }
        return nameMap;
    }

    public void flushCache() {
        propertyNameCache.clear();
    }
}
