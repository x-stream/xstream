/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters;

import java.util.Iterator;

/**
 * To aid debugging, some components are passed an ErrorWriter
 * when things go wrong, allowing them to add information
 * to the error message that may be helpful to diagnose problems.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public interface ErrorWriter {

    /**
     * Add some information to the error message.
     *
     * @param name        something to identify the type of information (e.g. 'XPath').
     * @param information detail of the message (e.g. '/blah/moo[3]'
     */
    void add(String name, String information);

    /**
     * Retrieve information of the error message.
     * 
     * @param errorKey the key of the message
     * @return the value
     * @since 1.3
     */
    String get(String errorKey);

    /**
     * Retrieve an iterator over all keys of the error message.
     * 
     * @return an Iterator
     * @since 1.3
     */
    Iterator keys();
}
