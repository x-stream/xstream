/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2012, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 16. September 2005 by Mauro Talevi
 */
package com.thoughtworks.xstream.annotations;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterMatcher;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to declare a converter. The annotation supports additionally the injection of
 * various constructor arguments provided by XStream:
 * <ul>
 * <li>{@link com.thoughtworks.xstream.mapper.Mapper}: The current mapper chain of the XStream
 * instance.</li>
 * <li>{@link com.thoughtworks.xstream.core.ClassLoaderReference}: The reference to the class
 * loader used by the XStream instance to deserialize the objects.</li>
 * <li>{@link com.thoughtworks.xstream.converters.reflection.ReflectionProvider}: The reflection
 * provider used by the reflection based converters of the current XStream instance.</li>
 * <li>{@link com.thoughtworks.xstream.converters.ConverterLookup}: The lookup for converters
 * handling a special type.</li>
 * <li>All elements provided with the individual arrays of this annotation. The provided values
 * follow the declaration sequence if a constructor requires multiple arguments of the same
 * type.</li>
 * <li>{@link Class}: The type of the element where the annotation is declared. Note, that this
 * argument is not supported when using
 * {@link com.thoughtworks.xstream.annotations.XStreamConverters} or {@link #useImplicitType()}
 * == false.</li>
 * <li>{@link com.thoughtworks.xstream.core.JVM}: Utility e.g. to load classes.</li>
 * <li>{@link ClassLoader} (deprecated since 1.4.5): The class loader used by the XStream
 * instance to deserialize the objects. Use ClassLoaderReference as argument</li>
 * </ul>
 * <p>
 * The algorithm will try the converter's constructor with the most arguments first.
 * </p>
 * <p>
 * Note, the annotation matches a {@link ConverterMatcher}.
 * {@link com.thoughtworks.xstream.converters.ConverterMatcher} as well as
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

    int priority() default XStream.PRIORITY_NORMAL;

    /**
     * Flag to provide the current type as implicit first Class argument to a converter's
     * constructor.
     * 
     * @return true if the current type is provided
     * @since 1.4.5
     */
    boolean useImplicitType() default true;

    /**
     * Provide class types as arguments for the converter's constructor arguments.
     * <p>
     * Note, that XStream itself provides the current class type as first Class argument to a
     * constructor, if the annotation is added directly to a class type (and not as part of a
     * parameter declaration of a {@link XStreamConverters} annotation). The current type has
     * precedence over any type provided with this method. This behavior can be overridden
     * setting {@link #useImplicitType()} to false.
     * 
     * @return the types
     * @since 1.4.2
     */
    Class<?>[] types() default {};

    String[] strings() default {};

    byte[] bytes() default {};

    char[] chars() default {};

    short[] shorts() default {};

    int[] ints() default {};

    long[] longs() default {};

    float[] floats() default {};

    double[] doubles() default {};

    boolean[] booleans() default {};
}
