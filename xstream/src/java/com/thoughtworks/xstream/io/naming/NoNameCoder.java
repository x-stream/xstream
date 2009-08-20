/*
 * Copyright (C) 2009 XStream Committers.
 * All rights reserved.
 *
 * Created on 15. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.naming;

/**
 * A NameCoder that does nothing.
 * <p>
 * The usage of this implementation implies that the names used for the objects can also be used
 * in the target format without any change. This applies also for XML if the object graph
 * contains no object that is an instance of an inner class type or is in the default package.
 * </p>
 * 
 * @author J&ouml;rg Schaiblea
 * @since upcoming
 */
public class NoNameCoder implements NameCoder {

    /**
     * {@inheritDoc}
     */
    public String decodeAttribute(String attributeName) {
        return attributeName;
    }

    /**
     * {@inheritDoc}
     */
    public String decodeNode(String nodeName) {
        return nodeName;
    }

    /**
     * {@inheritDoc}
     */
    public String encodeAttribute(String name) {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public String encodeNode(String name) {
        return name;
    }

}
