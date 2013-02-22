/*
 * Copyright (C) 2006, 2007, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. February 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.core.util.CompositeClassLoader;

import junit.framework.TestCase;

public class XmlFriendlyMapperTest extends TestCase {

    private Mapper mapper;

    public void testPrefixesIllegalXmlElementNamesWithValue() throws ClassNotFoundException {
        mapper = new XmlFriendlyMapper(new DefaultMapper(new CompositeClassLoader()));
        Class clsInDefaultPackage = Class.forName("$Package");
        String aliasedName = mapper.serializedClass(clsInDefaultPackage);
        assertTrue("Does not start with 'default-Package' : <" + aliasedName + ">",
                aliasedName.startsWith("default-Package"));
        assertEquals(clsInDefaultPackage, mapper.realClass(aliasedName));
    }

}
