/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2018 XStream Committers.
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
 * @author Paul Hammant
 * @deprecated As of 1.4.11, this interface will be merged into parent with version 1.5.0.
 */
public interface ExtendedHierarchicalStreamWriter extends HierarchicalStreamWriter {

    void startNode(String name, Class clazz);    

}
