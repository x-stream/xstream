/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters;

public interface UnmarshallingContext extends DataHolder {

    Object convertAnother(Object current, Class type);

    /**
     * @since 1.2
     */
    Object convertAnother(Object current, Class type, Converter converter);

    Object currentObject();

    Class getRequiredType();

    void addCompletionCallback(Runnable work, int priority);
    
}
