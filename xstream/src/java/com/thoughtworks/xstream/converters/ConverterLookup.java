/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters;

/**
 * Responsible for looking up the correct Converter implementation for a specific type.
 *
 * @author Joe Walnes
 * @see Converter
 */
public interface ConverterLookup {

    /**
     * Lookup a converter for a specific type.
     * <p/>
     * This type may be any Class, including primitive and array types. It may also be null, signifying
     * the value to be converted is a null type.
     */
    Converter lookupConverterForType(Class type);
}
