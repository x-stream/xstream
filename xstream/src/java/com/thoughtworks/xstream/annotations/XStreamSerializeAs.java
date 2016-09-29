/*
* Copyright (C) 2008 XStream Committers.
* All rights reserved.
*
* The software in this package is published under the terms of the BSD
* style license a copy of which has been included with this distribution in
* the LICENSE.txt file.
*
*/
package com.thoughtworks.xstream.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Designates that the type and its derived types will serialize as the specified type. This is useful if you want an
 * entire family of types serialize as the base type with its special converter.
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface XStreamSerializeAs {

    /**
     * Use {@code void.class} to cancel out the annotation defined in the super type.
     */
    Class<?> value();
}