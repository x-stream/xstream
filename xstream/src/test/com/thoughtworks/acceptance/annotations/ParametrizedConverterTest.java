/*
 * Copyright (C) 2008, 2009, 2011, 2012 XStream Committers.
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
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamConverters;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;
import com.thoughtworks.xstream.converters.extended.ToStringConverter;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
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
        xstream.alias("type", Type.class);
        xstream.processAnnotations(MyMap.class);
        xstream.processAnnotations(DerivedType.class);
        xstream.processAnnotations(SimpleBean.class);
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

    @XStreamConverters({
        @XStreamConverter(value = MyMapConverter.class, priority = XStream.PRIORITY_NORMAL + 1, types = {MyMap.class})
    })
    public static class MyMap extends HashMap<String, Object> {
    }

    public static class MyMapConverter extends MapConverter {

        private final Class<?> myType;

        public MyMapConverter(Mapper classMapper, Class<?> myType) {
            super(classMapper);
            this.myType = myType;
        }

        public boolean canConvert(Class type) {
            return type.equals(myType);
        }

    }

    /**
     * Tests a class-level XStreamConverter annotation subclassed from BigDecimal
     */
    public void testCanUseCurrentTypeAsParameter() {
        final Decimal value = new Decimal("5.5");
        String expected = "<decimal>5.5</decimal>";

        assertBothWays(value, expected);
    }

    /**
     * Tests three field-level XStreamConverter annotations for different types, which guarantees
     * the internal converterCache on AnnotationMapper is functioning properly.
     */
    public void testSameConverterWithDifferentType() {
        final Type value = new Type(new Decimal("1.5"), new Boolean(true));
        String expected = ""
            + "<type>\n"
            + "  <decimal>1.5</decimal>\n"
            + "  <bool>true</bool>\n"
            + "  <agreement>yes</agreement>\n"
            + "</type>";

        assertBothWays(value, expected);
    }

    @XStreamConverter(ToStringConverter.class)
    public static class Decimal extends BigDecimal {
        public Decimal(String str) {
            super(str);
        }
    }

    public static class Type {
        @XStreamConverter(ToStringConverter.class)
        private Decimal decimal = null;
        @XStreamConverter(ToStringConverter.class)
        private Boolean bool = null;
        @XStreamConverter(value=BooleanConverter.class, booleans={true}, strings={"yes", "no"})
        private Boolean agreement = null;

        public Type(Decimal decimal, Boolean bool) {
            this.decimal = decimal;
            this.bool = bool;
            this.agreement = bool;
        }
    }

    public void testConverterWithSecondTypeParameter() {
        final Type value = new DerivedType(new Decimal("1.5"), new Boolean(true));
        String expected = "<dtype bool='true' agreement='yes'>1.5</dtype>".replace('\'', '"');
        assertBothWays(value, expected);
    }
    
    @XStreamAlias("dtype")
    @XStreamConverter(value=ToAttributedValueConverter.class, types={Type.class}, strings={"decimal"})
    public static class DerivedType extends Type {

        public DerivedType(Decimal decimal, Boolean bool) {
            super(decimal, bool);
        }
        
    }

    public void testAnnotatedJavaBeanConverter() {
        final SimpleBean value = new SimpleBean();
        value.setName("joe");
        String expected = ""
                + "<bean>\n"
                + "  <name>joe</name>\n"
                + "</bean>";
        assertBothWays(value, expected);
    }
    
    
    @XStreamAlias("bean")
    @XStreamConverter(JavaBeanConverter.class)
    public static class SimpleBean extends StandardObject {
        private String myName;

        public String getName() {
            return myName;
        }

        public void setName(String name) {
            myName = name;
        }
    }
}
