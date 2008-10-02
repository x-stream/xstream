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

import com.thoughtworks.xstream.converters.ConverterMatcher;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to declare a converter.
 * <p>
 * Note, the annotation matches a {@link ConverterMatcher}.
 * {@link com.thoughtworks.xstream.converters.ConverterMatche} as well as
 * {@link com.thoughtworks.xstream.converters.SingleValueConverter} extend this interface. The
 * {@link com.thoughtworks.xstream.mapper.AnnotationMapper} can only handle these two
 * <strong>known</strong> types.
 * </p>
 * 
 * @author Chung-Onn Cheong
 * @author J&ouml;rg Schaible
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface XStreamConverter {
    Class<? extends ConverterMatcher> value();
}
