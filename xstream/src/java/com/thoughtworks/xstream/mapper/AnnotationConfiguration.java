/*
 * Copyright (C) 2007 XStream Committers.
 * Created on 07.11.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

/**
 * An interface for the configuration part of the AnnotationMapper.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public interface AnnotationConfiguration {

    void autodetectAnnotations(boolean mode);

    void processAnnotations(Class[] types);

}
