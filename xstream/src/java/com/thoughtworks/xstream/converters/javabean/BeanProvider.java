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
import com.thoughtworks.xstream.core.util.OrderRetainingMap;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;


public class BeanProvider {

    protected static final Object[] NO_PARAMS = new Object[0];
    private final Comparator propertyNameComparator;
    private final transient Map propertyNameCache = new WeakHashMap();

    public BeanProvider() {
        this(null);
    }

    public BeanProvider(final Comparator propertyNameComparator) {
        this.propertyNameComparator = propertyNameComparator;
    }
    
    public Object newInstance(Class type) {
        try {
            return getDefaultConstrutor(type).newInstance(NO_PARAMS);
        } catch (InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)e.getTargetException();
            } else if (e.getTargetException() instanceof Error) {
                throw (Error)e.getTargetException();
            } else {
                throw new ObjectAccessException("Constructor for "
                    + type.getName()
                    + " threw an exception", e);
            }
        }
    }

    public void visitSerializableProperties(Object object, Visitor visitor) {
        PropertyDescriptor[] propertyDescriptors = getSerializableProperties(object);
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor property = propertyDescriptors[i];
            try {
                Method readMethod = property.getReadMethod();
                Object value = readMethod.invoke(object, new Object[0]);
                visitor.visit(property.getName(), property.getPropertyType(), readMethod
                    .getDeclaringClass(), value);
            } catch (IllegalArgumentException e) {
                throw new ObjectAccessException("Could not get property "
                    + object.getClass()
                    + "."
                    + property.getName(), e);
            } catch (IllegalAccessException e) {
                throw new ObjectAccessException("Could not get property "
                    + object.getClass()
                    + "."
                    + property.getName(), e);
            } catch (InvocationTargetException e) {
                throw new ObjectAccessException("Could not get property "
                    + object.getClass()
                    + "."
                    + property.getName(), e);
            }
        }
    }

    public void writeProperty(Object object, String propertyName, Object value) {
        PropertyDescriptor property = getProperty(propertyName, object.getClass());
        try {
            property.getWriteMethod().invoke(object, new Object[]{value});
        } catch (IllegalArgumentException e) {
            throw new ObjectAccessException("Could not set property "
                + object.getClass()
                + "."
                + property.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Could not set property "
                + object.getClass()
                + "."
                + property.getName(), e);
        } catch (InvocationTargetException e) {
            throw new ObjectAccessException("Could not set property "
                + object.getClass()
                + "."
                + property.getName(), e);
        }
    }

    public Class getPropertyType(Object object, String name) {
        return getProperty(name, object.getClass()).getPropertyType();
    }

    public boolean propertyDefinedInClass(String name, Class type) {
        return getProperty(name, type) != null;
    }

    /**
     * Returns true if the Bean provider can instantiate the specified class
     */
    public boolean canInstantiate(Class type) {
        return getDefaultConstrutor(type) != null;
    }

    /**
     * Returns the default constructor, or null if none is found
     * 
     * @param type
     */
    protected Constructor getDefaultConstrutor(Class type) {
        Constructor[] constructors = type.getConstructors();
        for (int i = 0; i < constructors.length; i++) {
            Constructor c = constructors[i];
            if (c.getParameterTypes().length == 0 && Modifier.isPublic(c.getModifiers()))
                return c;
        }
        return null;
    }

    private PropertyDescriptor[] getSerializableProperties(Object object) {
        Map nameMap = getNameMap(object.getClass());
        List result = new ArrayList(nameMap.size());
        Set names = nameMap.keySet();
        if (propertyNameComparator != null) {
            Set sortedSet = new TreeSet(propertyNameComparator);
            sortedSet.addAll(names);
            names = sortedSet;
        }
        for (final Iterator iter = names.iterator(); iter.hasNext();) {
            final PropertyDescriptor descriptor = (PropertyDescriptor)nameMap.get(iter.next());
            if (canStreamProperty(descriptor)) {
                result.add(descriptor);
            }
        }
        return (PropertyDescriptor[])result.toArray(new PropertyDescriptor[result.size()]);
    }

    protected boolean canStreamProperty(PropertyDescriptor descriptor) {
        return descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null;
    }

    public boolean propertyWriteable(String name, Class type) {
        PropertyDescriptor property = getProperty(name, type);
        return property.getWriteMethod() != null;
    }

    private PropertyDescriptor getProperty(String name, Class type) {
        return (PropertyDescriptor)getNameMap(type).get(name);
    }

    private Map getNameMap(Class type) {
        Map nameMap = (Map)propertyNameCache.get(type);
        if (nameMap == null) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(type, Object.class);
            } catch (IntrospectionException e) {
                throw new ObjectAccessException("", e);
            }
            nameMap = new OrderRetainingMap();
            propertyNameCache.put(type, nameMap);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                nameMap.put(descriptor.getName(), descriptor);
            }
        }
        return nameMap;
    }

    interface Visitor {
        void visit(String name, Class type, Class definedIn, Object value);
    }
}
