/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 */
package com.thoughtworks.xstream.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to declare a field that is not Serializable (in terms of {@link java.io.Serializable}), and as such
 * is marked as <code>transient</code>, but XStream can serialize using adequate Converter.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface XStreamSerializable {

}