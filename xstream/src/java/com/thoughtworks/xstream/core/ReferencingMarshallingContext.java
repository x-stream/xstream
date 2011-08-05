/*
 * Copyright (C) 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 06. May 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.path.Path;

/**
 * A {@link MarshallingContext} that manages references. 
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public interface ReferencingMarshallingContext extends MarshallingContext {
    
    /**
     * Retrieve the current path.
     * 
     * @return the current path
     * @since 1.4
     */
    Path currentPath();
    
    /**
     * Request the reference key for the given item 
     * 
     * @param item the item to lookup
     * @return the reference key or <code>null</code>
     * @since 1.4
     */
    Object lookupReference(Object item);
    
    /**
     * Replace the currently marshalled item.
     * 
     * <p><strong>Use this method only, if you know exactly what you do!</strong> It is a special solution for
     * Serializable types that make usage of the writeReplace method where the replacing object itself is referenced.</p>
     * 
     * @param original the original item to convert
     * @param replacement the replacement item that is converted instead
     * @since 1.4
     */
    void replace(Object original, Object replacement);

    /**
     * Register an implicit element. This is typically some kind of collection.
     * 
     * @param item the object that is implicit
     * @since 1.4
     */
    void registerImplicit(Object item);
}
