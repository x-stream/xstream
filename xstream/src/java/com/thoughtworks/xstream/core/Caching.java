/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 19. July 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.core;

/**
 * Marker interface for caching implementations.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public interface Caching {
    void flushCache();
}
