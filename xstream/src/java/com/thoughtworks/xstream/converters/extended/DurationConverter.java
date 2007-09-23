/*
 * Copyright (C) 2007 XStream committers.
 * Created on 21.09.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;


/**
 * A Converter for the XML Schema datatype <a
 * href="http://www.w3.org/TR/xmlschema-2/#duration">duration</a> and the Java type
 * {@link javax.xml.datatype.Duration Duration}.
 * 
 * @author John Kristian
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class DurationConverter extends AbstractSingleValueConverter {
    private final DatatypeFactory factory;

    public DurationConverter() throws DatatypeConfigurationException {
        this(DatatypeFactory.newInstance());
    }

    public DurationConverter(DatatypeFactory factory) {
        this.factory = factory;
    }

    public boolean canConvert(Class c) {
        return factory != null && Duration.class.isAssignableFrom(c);
    }

    public Object fromString(String s) {
        return factory.newDuration(s);
    }
}
