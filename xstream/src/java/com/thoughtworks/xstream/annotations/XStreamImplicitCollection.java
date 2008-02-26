/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 16. September 2005 by Mauro Talevi
 */
package com.thoughtworks.xstream.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Chung-Onn Cheong
 * @deprecated since 1.3, use @XStreamImplicit at field level
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XStreamImplicitCollection {
    String value(); //fieldName
    String item() default "";   //itemfieldName
}
