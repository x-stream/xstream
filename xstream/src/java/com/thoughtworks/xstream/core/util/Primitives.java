/*
 * Copyright (c) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 11. October 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for primitives.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.1
 */
public final class Primitives {
    private final static Map BOX = new HashMap();
    private final static Map UNBOX = new HashMap();
    
    static {
        final Class[][] boxing = new Class[][]{
            { byte.class, Byte.class},
            { char.class, Character.class},
            { short.class, Short.class},
            { int.class, Integer.class},
            { long.class, Long.class},
            { float.class, Float.class},
            { double.class, Double.class},
            { boolean.class, Boolean.class},
            { void.class, Void.class},
        };
        for (int i = 0; i < boxing.length; i++) {
            BOX.put(boxing[i][0], boxing[i][1]);
            UNBOX.put(boxing[i][1], boxing[i][0]);
        }
    }
    
    static public Class box(final Class type) {
        return (Class)BOX.get(type);
    }
    
    static public Class unbox(final Class type) {
        return (Class)UNBOX.get(type);
    }
}
