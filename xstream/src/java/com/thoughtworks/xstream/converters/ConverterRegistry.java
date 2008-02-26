/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters;

/**
 * An interface for the converter management.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public interface ConverterRegistry {

    void registerConverter(Converter converter, int priority);

}
