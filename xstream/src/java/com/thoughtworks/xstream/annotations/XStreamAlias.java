/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 11. August 2005 by Mauro Talevi
 */
package com.thoughtworks.xstream.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define an XStream class or field alias.
 *
 * @author Emil Kirschner
 * @author Chung-Onn Cheong
 * @see com.thoughtworks.xstream.XStream#alias(String, Class)
 * @see com.thoughtworks.xstream.XStream#alias(String, Class, Class)
 * @see com.thoughtworks.xstream.XStream#addDefaultImplementation(Class, Class)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface XStreamAlias {
    /**
     * The name of the class or field alias.
     */
    public String value();
    /**
     * A possible default implementation if the annotated type is an interface.
     */
    public Class<?> impl() default Void.class; //Use Void to denote as Null
}
