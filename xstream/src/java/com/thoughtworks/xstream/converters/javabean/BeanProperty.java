/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 12. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.javabean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;


/**
 * Provide access to a bean property.
 * 
 * @author <a href="mailto:andrea.aime@aliceposta.it">Andrea Aime</a>
 * @deprecated As of 1.3.1, no longer in use
 */
@Deprecated
public class BeanProperty {

    /** the target class */
    private final Class<?> memberClass;

    /** the property name */
    private final String propertyName;

    /** the property type */
    private final Class<?> type;

    /** the getter */
    protected Method getter;

    /** the setter */
    private Method setter;

    /**
     * Creates a new {@link BeanProperty}that gets the specified property from the specified class.
	 * @param memberClass
	 * @param propertyType
	 * @param propertyName
     */
    public BeanProperty(final Class<?> memberClass, final String propertyName, final Class<?> propertyType) {
        this.memberClass = memberClass;
        this.propertyName = propertyName;
        type = propertyType;
    }

    /**
     * Gets the base class that this getter accesses.
	 * @return 
     */
    public Class<?> getBeanClass() {
        return memberClass;
    }

    /**
     * Returns the property type
	 * @return 
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Gets the name of the property that this getter extracts.
	 * @return 
     */
    public String getName() {
        return propertyName;
    }

    /**
     * Gets whether this property can get get.
	 * @return 
     */
    public boolean isReadable() {
        return getter != null;
    }

    /**
     * Gets whether this property can be set.
	 * @return 
     */
    public boolean isWritable() {
        return setter != null;
    }

    /**
     * Gets the value of this property for the specified Object.
     * 
	 * @param member
	 * @return 
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public Object get(final Object member) throws IllegalArgumentException, IllegalAccessException {
        if (!isReadable()) {
            throw new IllegalStateException("Property " + propertyName + " of " + memberClass + " not readable");
        }

        try {
            return getter.invoke(member);
        } catch (final InvocationTargetException e) {
            throw new UndeclaredThrowableException(e.getTargetException());
        }
    }

    /**
     * Sets the value of this property for the specified Object.
     * 
	 * @param member
	 * @param newValue
	 * @return 
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public Object set(final Object member, final Object newValue)
            throws IllegalArgumentException, IllegalAccessException {
        if (!isWritable()) {
            throw new IllegalStateException("Property " + propertyName + " of " + memberClass + " not writable");
        }

        try {
            return setter.invoke(member, newValue);
        } catch (final InvocationTargetException e) {
            throw new UndeclaredThrowableException(e.getTargetException());
        }
    }

    /**
     * @param method
     */
    public void setGetterMethod(final Method method) {
        getter = method;

    }

    /**
     * @param method
     */
    public void setSetterMethod(final Method method) {
        setter = method;
    }
}
