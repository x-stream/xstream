/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
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
 * Annotation used to notify Annotations.configureAliases that it should recursively invoke itself for
 * all parameterized types of this field.
 *
 * @author Emil Kirschner
 * @author Chung-Onn Cheong
 * @deprecated since 1.3, recursive behaviour is now always used and the annotation is therefore superfluous
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Deprecated
public @interface XStreamContainedType {

}
