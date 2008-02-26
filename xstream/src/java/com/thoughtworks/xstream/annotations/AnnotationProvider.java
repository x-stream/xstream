/*
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. March 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


/**
 * An utility class to provide annotations from different sources
 * 
 * @author Guilherme Silveira
 * @deprecated since 1.3
 */
@Deprecated
public class AnnotationProvider {

    /**
     * Returns a field annotation based on an annotation type
     * 
     * @param field the annotation Field
     * @param annotationClass the annotation Class
     * @return The Annotation type
     * @deprecated since 1.3
     */
    @Deprecated
    public <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

}
