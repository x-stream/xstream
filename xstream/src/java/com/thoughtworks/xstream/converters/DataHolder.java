/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 16. August 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters;

import java.util.Iterator;

/**
 * Holds generic data, to be used as seen fit by the user.
 *
 * @author Joe Walnes
 */
public interface DataHolder {

    Object get(Object key);
    void put(Object key, Object value);
    Iterator keys();

}
