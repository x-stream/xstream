/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. June 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io;

/**
 * @deprecated As of 1.4.11, this helper is no longer required since version 1.5.0.
 */
@Deprecated
public class ExtendedHierarchicalStreamWriterHelper {

    /**
     * @deprecated As of 1.4.11, with version 1.5.0 use {@link HierarchicalStreamWriter#startNode(String, Class)}
     *             directly. This helper will be no longer required.
     */
    @Deprecated
    public static void startNode(final HierarchicalStreamWriter writer, final String name, final Class<?> clazz) {
        writer.startNode(name, clazz);
    }
}
