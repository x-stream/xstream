/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. July 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.tools.benchmark;

/**
 * Provides a target object to use in the metric. This could be a very small object or a large
 * complicated graph.
 *
 * Also used to test if the object is equal to another instance (as some object's don't provide
 * sensible equals() methods.
 *  
 * @author Joe Walnes
 * @see Harness
 */
public interface Target {

    /**
     * The target to use in the metric.
     */
    Object target();

    /**
     * Check whether the object for this target is equal to another one.
     */
    boolean isEqual(Object other);

}
