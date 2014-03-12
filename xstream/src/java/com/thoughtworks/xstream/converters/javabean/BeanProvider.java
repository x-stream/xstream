/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 12. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.javabean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;


public class BeanProvider implements JavaBeanProvider {

    /**
     * @deprecated As of 1.4.6
     */
    @Deprecated
    protected static final Object[] NO_PARAMS = new Object[0];
    protected PropertyDictionary propertyDictionary;

    /**
     * Construct a BeanProvider that will process the bean properties in their natural order.
     */
    public BeanProvider() {
        this(new PropertyDictionary(new NativePropertySorter()));
    }

    /**
     * Construct a BeanProvider with a comparator to sort the bean properties by name in the dictionary.
     * 
     * @param propertyNameComparator the comparator
     */
    public BeanProvider(final Comparator<String> propertyNameComparator) {
        this(new PropertyDictionary(new ComparingPropertySorter(propertyNameComparator)));
    }

    /**
     * Construct a BeanProvider with a provided property dictionary.
     * 
     * @param propertyDictionary the property dictionary to use
     * @since 1.4
     */
    public BeanProvider(final PropertyDictionary propertyDictionary) {
        this.propertyDictionary = propertyDictionary;
    }

    @Override
    public Object newInstance(final Class<?> type) {
        try {
            return type.newInstance();
        } catch (final InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (final SecurityException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (final ExceptionInInitializerError e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        }
    }

    @Override
    public void visitSerializableProperties(final Object object, final JavaBeanProvider.Visitor visitor) {
        final PropertyDescriptor[] propertyDescriptors = getSerializableProperties(object);
        for (final PropertyDescriptor property : propertyDescriptors) {
            try {
                final Method readMethod = property.getReadMethod();
                final String name = property.getName();
                final Class<?> definedIn = readMethod.getDeclaringClass();
                if (visitor.shouldVisit(name, definedIn)) {
                    final Object value = readMethod.invoke(object);
                    visitor.visit(name, property.getPropertyType(), definedIn, value);
                }
            } catch (final IllegalArgumentException e) {
                throw new ObjectAccessException("Could not get property "
                    + object.getClass()
                    + "."
                    + property.getName(), e);
            } catch (final IllegalAccessException e) {
                throw new ObjectAccessException("Could not get property "
                    + object.getClass()
                    + "."
                    + property.getName(), e);
            } catch (final InvocationTargetException e) {
                throw new ObjectAccessException("Could not get property "
                    + object.getClass()
                    + "."
                    + property.getName(), e);
            }
        }
    }

    @Override
    public void writeProperty(final Object object, final String propertyName, final Object value) {
        final PropertyDescriptor property = getProperty(propertyName, object.getClass());
        try {
            property.getWriteMethod().invoke(object, new Object[]{value});
        } catch (final IllegalArgumentException e) {
            throw new ObjectAccessException("Could not set property " + object.getClass() + "." + property.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new ObjectAccessException("Could not set property " + object.getClass() + "." + property.getName(), e);
        } catch (final InvocationTargetException e) {
            throw new ObjectAccessException("Could not set property " + object.getClass() + "." + property.getName(), e);
        }
    }

    @Override
    public Class<?> getPropertyType(final Object object, final String name) {
        return getProperty(name, object.getClass()).getPropertyType();
    }

    @Override
    public boolean propertyDefinedInClass(final String name, final Class<?> type) {
        return getProperty(name, type) != null;
    }

    /**
     * Returns true if the Bean provider can instantiate the specified class
     */
    @Override
    public boolean canInstantiate(final Class<?> type) {
        try {
            return newInstance(type) != null;
        } catch (final ObjectAccessException e) {
            return false;
        }
    }

    /**
     * Returns the default constructor, or null if none is found
     * 
     * @param type
     * @deprecated As of 1.4.6 use {@link #newInstance(Class)} or {@link #canInstantiate(Class)} directly.
     */
    @Deprecated
    protected Constructor<?> getDefaultConstrutor(final Class<?> type) {

        final Constructor<?>[] constructors = type.getConstructors();
        for (final Constructor<?> c : constructors) {
            if (c.getParameterTypes().length == 0 && Modifier.isPublic(c.getModifiers())) {
                return c;
            }
        }
        return null;
    }

    protected PropertyDescriptor[] getSerializableProperties(final Object object) {
        final List<PropertyDescriptor> result = new ArrayList<PropertyDescriptor>();
        for (final Iterator<PropertyDescriptor> iter = propertyDictionary.propertiesFor(object.getClass()); iter
            .hasNext();) {
            final PropertyDescriptor descriptor = iter.next();
            if (canStreamProperty(descriptor)) {
                result.add(descriptor);
            }
        }
        return result.toArray(new PropertyDescriptor[result.size()]);
    }

    protected boolean canStreamProperty(final PropertyDescriptor descriptor) {
        return descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null;
    }

    public boolean propertyWriteable(final String name, final Class<?> type) {
        final PropertyDescriptor property = getProperty(name, type);
        return property.getWriteMethod() != null;
    }

    protected PropertyDescriptor getProperty(final String name, final Class<?> type) {
        return propertyDictionary.propertyDescriptor(type, name);
    }

    /**
     * @deprecated As of 1.4 use {@link JavaBeanProvider.Visitor}
     */
    @Deprecated
    public interface Visitor extends JavaBeanProvider.Visitor {}
}
