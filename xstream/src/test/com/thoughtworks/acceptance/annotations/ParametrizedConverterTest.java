/*
 * Copyright (C) 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. January 2008 by Joerg Schaible
 */
package com.thoughtworks.acceptance.annotations;

import java.math.BigDecimal;
import java.util.HashMap;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamConverters;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.extended.ToStringConverter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Tests for using annotations for classes.
 * 
 * @author Chung-Onn, Cheong
 * @author J&ouml;rg Schaible
 * @author Jason Greanya
 */
public class ParametrizedConverterTest extends AbstractAcceptanceTest {

    @Override
    protected XStream createXStream() {
        XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("my-map", MyMap.class);
        xstream.alias("decimal", Decimal.class);
        xstream.alias("pair", Pair.class);
        xstream.processAnnotations(MyMap.class);
        xstream.processAnnotations(Pair.class);
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

    /**
     * Tests a class-level XStreamConverter annotation subclassed from BigDecimal
     */
    public void testCanUseCurrentTypAsParameter() {
        final Decimal value = new Decimal("5.5");
        String expected = "<decimal>5.5</decimal>";

        assertBothWays(value, expected);
    }

    /**
     * Tests two field-level XStreamConverter annotations for different types, which guarantees
     * the internal converterCache on AnnotationMapper is functioning properly.
     */
    public void testSameConvertrWithDifferentType() {
        final Pair value = new Pair(new Decimal("1.5"), new Boolean(true));
        String expected = ""
            + "<pair>\n"
            + "  <decimal>1.5</decimal>\n"
            + "  <bool>true</bool>\n"
            + "</pair>";

        assertBothWays(value, expected);
    }

    @XStreamConverter(ToStringConverter.class)
    public static class Decimal extends BigDecimal {
        public Decimal(String str) {
            super(str);
        }
    }

    public static class Pair {
        @XStreamConverter(ToStringConverter.class)
        private Decimal decimal = null;
        @XStreamConverter(ToStringConverter.class)
        private Boolean bool = null;

        public Pair(Decimal decimal, Boolean bool) {
            this.decimal = decimal;
            this.bool = bool;
        }
    }
}
