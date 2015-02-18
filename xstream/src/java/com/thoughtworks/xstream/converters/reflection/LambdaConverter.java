/*
 * Copyright (C) 2015 XStream Committer.
 * All rights reserved.
 *
 * Created on 17. January 2015 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import java.io.Serializable;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.Types;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a lambda type.
 * 
 * The implementation maps any non-serializable lambda instance to {@code null}.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.8
 */
public class LambdaConverter extends SerializableConverter {

    /**
     * Constructs a LambdaConverter.
     * 
     * @param mapper
     * @param reflectionProvider
     * @param classLoaderReference
     * @since 1.4.8
     */
    public LambdaConverter(
            final Mapper mapper, final ReflectionProvider reflectionProvider,
            final ClassLoaderReference classLoaderReference) {
        super(mapper, reflectionProvider, classLoaderReference);
    }

    @Override
    public boolean canConvert(final Class type) {
        return Types.isLambdaType(type)
            && (JVM.canCreateDerivedObjectOutputStream() || !Serializable.class.isAssignableFrom(type));
    }

    @Override
    public void marshal(final Object original, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        if (original instanceof Serializable) {
            super.marshal(original, writer, context);
        }
    }
}
