/*
 * Copyright (C) 2006 Jörg Schaible
 * Created on 13.02.2006 by Jörg Schaible
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
