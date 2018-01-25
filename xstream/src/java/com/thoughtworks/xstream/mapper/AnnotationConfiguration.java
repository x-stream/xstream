/*
 * Copyright (C) 2007, 2008, 2013, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. November 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

/**
 * A helper interface for the configuration part of the AnnotationMapper.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 * @deprecated As of 1.4.5, minimal JDK version will be 1.7 for next major release and AnnotationMapper can be used
 *             directly
 */
@Deprecated
public interface AnnotationConfiguration {

    void autodetectAnnotations(boolean mode);

    void processAnnotations(Class<?>... types);

}
