/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. January 2008 by Joerg Schaible
 */
package com.thoughtworks.acceptance.annotations;

import java.util.HashMap;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamConverters;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Tests for using annotations for classes.
 * 
 * @author Chung-Onn, Cheong
 * @author J&ouml;rg Schaible
 */
public class ClassConverterTest extends AbstractAcceptanceTest {

    @Override
    protected XStream createXStream() {
        XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("my-map", MyMap.class);
        xstream.processAnnotations(MyMap.class);
    }

    public void testAnnotationForConvertersWithParameters() {
        final MyMap value = new MyMap();
        value.put("key1", "value1");
        String expected = ""
            + "<my-map>\n" 
            + "  <entry>\n" 
            + "    <string>key1</string>\n" 
            + "    <string>value1</string>\n" 
            + "  </entry>\n" 
            + "</my-map>";
        assertBothWays(value, expected);
    }

    @XStreamConverters({@XStreamConverter(MyMapConverter.class)})
    public static class MyMap extends HashMap<String, Object> {

    }

    public static class MyMapConverter extends MapConverter {

        public MyMapConverter(Mapper classMapper) {
            super(classMapper);
        }

        public boolean canConvert(Class type) {
            return type.equals(MyMap.class);
        }

    }
}
