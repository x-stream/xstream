/*
 * Copyright (C) 2006, 2007, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. December 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for marking a field as an implicit collection or array.
 *
 * @author Lucio Benfante
 * @author J&ouml;rg Schaible
 * @since 1.2.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XStreamImplicit {
    /**
     * Element name of the implicit collection.
     */
    String itemFieldName() default "";
    /**
     * Field name of map entries that are used as key for the element in the implicit map.
     * @since 1.4
     */
    String keyFieldName() default "";
}
