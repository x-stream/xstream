/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 12. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.javabean;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Builds the serializable properties maps for each bean and caches them.
 */
public class PropertyDictionary {

    private final Map keyedByPropertyNameCache = Collections.synchronizedMap(new HashMap());

    public Iterator serializablePropertiesFor(Class cls) {
        return buildMap(cls).values().iterator();
    }

    /**
     * Locates a serializable property
     * 
     * @param cls
     * @param name
     */
    public BeanProperty property(Class cls, String name) {
        Map properties = buildMap(cls);
        BeanProperty property = (BeanProperty) properties.get(name);
        if (property == null) {
            throw new ObjectAccessException("No such property " + cls.getName() + "." + name);
        } else {
            return property;
        }
    }

    /**
     * Builds the map of all serializable properties for the the provided bean
     * 
     * @param cls
     */
    private Map buildMap(Class cls) {
        final String clsName = cls.getName();
        if (!keyedByPropertyNameCache.containsKey(clsName)) {
            synchronized (keyedByPropertyNameCache) {
                if (!keyedByPropertyNameCache.containsKey(clsName)) { // double check
                    // Gather all the properties, using only the keyed map. It
                    // is possible that a class have two writable only
                    // properties that have the same name
                    // but different types
                    final Map propertyMap = new HashMap();
                    Method[] methods = cls.getMethods();

                    for (int i = 0; i < methods.length; i++) {
                        if (!Modifier.isPublic(methods[i].getModifiers())
                                || Modifier.isStatic(methods[i].getModifiers()))
                            continue;

                        String methodName = methods[i].getName();
                        Class[] parameters = methods[i].getParameterTypes();
                        Class returnType = methods[i].getReturnType();
                        String propertyName;
                        if ((methodName.startsWith("get") || methodName.startsWith("is"))
                                && parameters.length == 0 && returnType != void.class) {
                            if (methodName.startsWith("get")) {
                                propertyName = Introspector.decapitalize(methodName.substring(3));
                            } else {
                                propertyName = Introspector.decapitalize(methodName.substring(2));
                            }
                            BeanProperty property = getBeanProperty(propertyMap, cls, propertyName,
                                    returnType);
                            property.setGetterMethod(methods[i]);
                        } else if (methodName.startsWith("set") && parameters.length == 1
                                && returnType == void.class) {
                            propertyName = Introspector.decapitalize(methodName.substring(3));
                            BeanProperty property = getBeanProperty(propertyMap, cls, propertyName,
                                    parameters[0]);
                            property.setSetterMethod(methods[i]);
                        }
                    }

                    // retain only those that can be both read and written and
                    // sort them by name
                    List serializableProperties = new ArrayList();
                    for (Iterator it = propertyMap.values().iterator(); it.hasNext();) {
                        BeanProperty property = (BeanProperty) it.next();
                        if (property.isReadable() && property.isWritable()) {
                            serializableProperties.add(property);
                        }
                    }
                    Collections.sort(serializableProperties, new BeanPropertyComparator());

                    // build the maps and return
                    final Map keyedByFieldName = new OrderRetainingMap();
                    for (Iterator it = serializableProperties.iterator(); it.hasNext();) {
                        BeanProperty property = (BeanProperty) it.next();
                        keyedByFieldName.put(property.getName(), property);
                    }

                    keyedByPropertyNameCache.put(clsName, keyedByFieldName);
                }
            }
        }
        return (Map) keyedByPropertyNameCache.get(clsName);
    }

    private BeanProperty getBeanProperty(Map propertyMap, Class cls, String propertyName, Class type) {
        PropertyKey key = new PropertyKey(propertyName, type);
        BeanProperty property = (BeanProperty) propertyMap.get(key);
        if (property == null) {
            property = new BeanProperty(cls, propertyName, type);
            propertyMap.put(key, property);
        }
        return property;
    }

    /**
     * Needed to avoid problems with multiple setters with the same name, but
     * referred to different types
     */
    private static class PropertyKey {
        private String propertyName;

        private Class propertyType;

        public PropertyKey(String propertyName, Class propertyType) {
            this.propertyName = propertyName;
            this.propertyType = propertyType;
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof PropertyKey))
                return false;

            final PropertyKey propertyKey = (PropertyKey) o;

            if (propertyName != null ? !propertyName.equals(propertyKey.propertyName)
                    : propertyKey.propertyName != null)
                return false;
            if (propertyType != null ? !propertyType.equals(propertyKey.propertyType)
                    : propertyKey.propertyType != null)
                return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (propertyName != null ? propertyName.hashCode() : 0);
            result = 29 * result + (propertyType != null ? propertyType.hashCode() : 0);
            return result;
        }

        public String toString() {
            return "PropertyKey{propertyName='" + propertyName + "'" + ", propertyType="
                    + propertyType + "}";
        }

    }

    /**
     * Compares properties by name
     */
    private static class BeanPropertyComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            return ((BeanProperty) o1).getName().compareTo(((BeanProperty) o2).getName());
        }

    }

    private static class OrderRetainingMap extends HashMap {

        private List valueOrder = new ArrayList();

        public Object put(Object key, Object value) {
            valueOrder.add(value);
            return super.put(key, value);
        }

        public Collection values() {
            return Collections.unmodifiableList(valueOrder);
        }
    }

}