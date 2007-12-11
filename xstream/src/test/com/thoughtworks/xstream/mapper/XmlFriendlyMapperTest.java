/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. February 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.acceptance.objects.SampleDynamicProxy;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;

import junit.framework.TestCase;

public class XmlFriendlyMapperTest extends TestCase {

    private Mapper mapper;

    public void testPrefixesIllegalXmlElementNamesWithValue() {
        mapper = new XmlFriendlyMapper(new DefaultMapper(new CompositeClassLoader()));
        Class proxyCls = SampleDynamicProxy.newInstance().getClass();
        String aliasedName = mapper.serializedClass(proxyCls);
        assertTrue("Does not start with 'default-Proxy' : <" + aliasedName + ">",
                aliasedName.startsWith("default-Proxy"));
        assertEquals(proxyCls, mapper.realClass(aliasedName));
    }

}
