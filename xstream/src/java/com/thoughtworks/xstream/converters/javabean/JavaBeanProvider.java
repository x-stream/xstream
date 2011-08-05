/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. July 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.javabean;


/**
 * @author J&ouml;rg Schaible
 *
 * @since 1.4
 */
public interface JavaBeanProvider {

    Object newInstance(Class type);

    void visitSerializableProperties(Object object, Visitor visitor);

    void writeProperty(Object object, String propertyName, Object value);

    Class getPropertyType(Object object, String name);

    boolean propertyDefinedInClass(String name, Class type);

    /**
     * Returns true if the Bean provider can instantiate the specified class
     */
    boolean canInstantiate(Class type);

    public interface Visitor {
        boolean shouldVisit(String name, Class definedIn);
        void visit(String name, Class type, Class definedIn, Object value);
    }

}
