/*
 * Copyright (C) 2007 XStream committers.
 * All rights reserved.
 * 
 * Created on 08.12.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;

import javax.swing.LookAndFeel;

import java.io.NotSerializableException;


/**
 * A converter for Swing LookAndFeel implementations. The JDK's implementations are serializable
 * for historical reasons but will throw a {@link NotSerializableException} in their writeObject
 * method. Therefore XStream will use an implementation based on the ReflectionConverter.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class LookAndFeelConverter extends ReflectionConverter {

    /**
     * Constructs a LookAndFeelConverter.
     * 
     * @param mapper the mapper
     * @param reflectionProvider the reflection provider
     * @since upcoming
     */
    public LookAndFeelConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        super(mapper, reflectionProvider);
    }

    public boolean canConvert(Class type) {
        return LookAndFeel.class.isAssignableFrom(type);
    }
}
