/*
 * Copyright (C) 2004 Joe Walnes.
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

public interface MarshallingContext extends DataHolder {

	/**
	 * Converts another object searching for the default converter
	 * @param nextItem	the next item to convert
	 */
    void convertAnother(Object nextItem);
    
    /**
     * Converts another object using the specified converter
     * @param nextItem	the next item to convert
     * @param converter	the Converter to use
     * @since 1.2
     */
    void convertAnother(Object nextItem, Converter converter);

}
