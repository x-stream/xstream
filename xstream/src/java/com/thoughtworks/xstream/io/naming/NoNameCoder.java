/*
 * Copyright (C) 2009, 2011, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 15. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.naming;

/**
 * A NameCoder that does nothing.
 * <p>
 * The usage of this implementation implies that the names used for the objects can also be used in the target format
 * without any change. This applies also for XML if the object graph contains no object that is an instance of an inner
 * class type or is in the default package.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class NoNameCoder implements NameCoder {

    @Override
    public String decodeAttribute(final String attributeName) {
        return attributeName;
    }

    @Override
    public String decodeNode(final String nodeName) {
        return nodeName;
    }

    @Override
    public String encodeAttribute(final String name) {
        return name;
    }

    @Override
    public String encodeNode(final String name) {
        return name;
    }

}
