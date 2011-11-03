/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 13. October 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.io;

/**
 * @author J&ouml;rg Schaible
 * @since 1.4.2
 */
public interface ExtendedHierarchicalStreamReader extends HierarchicalStreamReader {

    /**
     * Peek the name of the next child. In situation where {@link #hasMoreChildren()} returns
     * true, peek the tag name of the child.
     * 
     * @since 1.4.2
     */
    String peekNextChild();
}
