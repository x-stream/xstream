/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. May 2005 by Guilherme Silveira
 */
package com.thoughtworks.xstream.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Declares a field to be omitted. The result is the same as invoking the method
 * omitField in a XStream instance.
 * 
 * @author Chung-Onn Cheong
 * @author Guilherme Silveira
 * @since 1.2.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @ interface XStreamOmitField {
}
