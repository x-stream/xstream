package com.thoughtworks.xstream.converters.javabean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * Provide access to a bean property.
 * 
 * @author <a href="mailto:andrea.aime@aliceposta.it">Andrea Aime</a>
 */
public class BeanProperty {

    /** the target class */
    private Class memberClass;

    /** the property name */
    private String propertyName;

    /** the property type */
    private Class type;

    /** the getter */
    protected Method getter;

    /** the setter */
    private Method setter;
    
    private static final Object[] EMPTY_ARGS = new Object[0];

    /**
     * Creates a new {@link BeanProperty}that gets the specified property from
     * the specified class.
     */
    public BeanProperty(Class memberClass, String propertyName, Class propertyType) {
        this.memberClass = memberClass;
        this.propertyName = propertyName;
        this.type = propertyType;
    }

    /**
     * Gets the base class that this getter accesses.
     */
    public Class getBeanClass() {
        return memberClass;
    }

    /**
     * Returns the property type
     */
    public Class getType() {
        return type;
    }

    /**
     * Gets the name of the property that this getter extracts.
     */
    public String getName() {
        return propertyName;
    }

    /**
     * Gets whether this property can get get.
     */
    public boolean isReadable() {
        return (getter != null);
    }

    /**
     * Gets whether this property can be set.
     */
    public boolean isWritable() {
        return (setter != null);
    }

    /**
     * Gets the value of this property for the specified Object.
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public Object get(Object member) throws IllegalArgumentException, IllegalAccessException {
        if (!isReadable())
            throw new IllegalStateException("Property " + propertyName + " of " + memberClass
                    + " not readable");

        try {
            return getter.invoke(member, EMPTY_ARGS);
        } catch (InvocationTargetException e) {
            throw new UndeclaredThrowableException(e.getTargetException());
        }
    }

    /**
     * Sets the value of this property for the specified Object.
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public Object set(Object member, Object newValue) throws IllegalArgumentException, IllegalAccessException {
        if (!isWritable())
            throw new IllegalStateException("Property " + propertyName + " of " + memberClass
                    + " not writable");

        try {
            return setter.invoke(member, new Object[] { newValue });
        } catch (InvocationTargetException e) {
            throw new UndeclaredThrowableException(e.getTargetException());
        }
    }

    /**
     * @param method
     */
    public void setGetterMethod(Method method) {
        this.getter = method;

    }

    /**
     * @param method
     */
    public void setSetterMethod(Method method) {
        this.setter = method;
    }
}