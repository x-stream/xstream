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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.ErrorWritingException;
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
        ErrorWritingException ex = null;
        if (type == void.class || type == Void.class) {
            ex = new ConversionException("Security alert: Marshalling rejected");
        } else {
            try {
                return type.newInstance();
            } catch (final InstantiationException | ExceptionInInitializerError e) {
                ex = new ConversionException("Cannot construct type", e);
            } catch (final IllegalAccessException e) {
                ex = new ObjectAccessException("Cannot construct type", e);
            } catch (final SecurityException e) {
                ex = new ObjectAccessException("Cannot construct type", e);
            }
        }
        ex.add("construction-type", type.getName());
        throw ex;
    }

    @Override
    public void visitSerializableProperties(final Object object, final JavaBeanProvider.Visitor visitor) {
        final PropertyDescriptor[] propertyDescriptors = getSerializableProperties(object);
        for (final PropertyDescriptor property : propertyDescriptors) {
            ErrorWritingException ex = null;
            try {
                final Method readMethod = property.getReadMethod();
                final String name = property.getName();
                final Class<?> definedIn = readMethod.getDeclaringClass();
                if (visitor.shouldVisit(name, definedIn)) {
                    final Object value = readMethod.invoke(object);
                    visitor.visit(name, property.getPropertyType(), definedIn, value);
                }
            } catch (final IllegalArgumentException e) {
                ex = new ConversionException("Cannot get property", e);
            } catch (final IllegalAccessException e) {
                ex = new ObjectAccessException("Cannot access property", e);
            } catch (final InvocationTargetException e) {
                ex = new ConversionException("Cannot get property", e.getTargetException());
            }
            if (ex != null) {
                ex.add("property", object.getClass() + "." + property.getName());
                throw ex;
            }
        }
    }

    @Override
    public void writeProperty(final Object object, final String propertyName, final Object value) {
        final PropertyDescriptor property = getProperty(propertyName, object.getClass());
        ErrorWritingException ex = null;
        try {
            property.getWriteMethod().invoke(object, value);
        } catch (final IllegalArgumentException e) {
            ex = new ConversionException("Cannot set property", e);
        } catch (final IllegalAccessException e) {
            ex = new ObjectAccessException("Cannot access property", e);
        } catch (final InvocationTargetException e) {
            ex = new ConversionException("Cannot set property", e.getTargetException());
        }
        if (ex != null) {
            ex.add("property", object.getClass() + "." + property.getName());
            throw ex;
        }
    }

    @Override
    public Class<?> getPropertyType(final Object object, final String name) {
        return getProperty(name, object.getClass()).getPropertyType();
    }

    @Override
    public boolean propertyDefinedInClass(final String name, final Class<?> type) {
        return propertyDictionary.propertyDescriptorOrNull(type, name) != null;
    }

    /**
     * Returns true if the Bean provider can instantiate the specified class
     */
    @Override
    public boolean canInstantiate(final Class<?> type) {
        try {
            return type != null && newInstance(type) != null;
        } catch (final ErrorWritingException e) {
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
        final List<PropertyDescriptor> result = new ArrayList<>();
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
