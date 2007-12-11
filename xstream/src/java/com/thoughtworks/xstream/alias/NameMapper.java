/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 27. Februar 2004 by Jason van Zyl
 */
package com.thoughtworks.xstream.alias;

/**
 * @deprecated As of 1.2
 */
public interface NameMapper {
    /**
     * @deprecated As of 1.2
     */
    String fromXML(String elementName);

    /**
     * @deprecated As of 1.2
     */
    String toXML(String fieldName);
}
