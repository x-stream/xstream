/*
 * Copyright (C) 2015 XStream Committers.
 * All rights reserved.
 *
 * Created on 15. January 2015 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.thoughtworks.xstream.core.util.Types;


/**
 * Mapper to map serializable lambda types to the name of their functional interface and non-serializable ones to
 * Mapper.Null.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.8
 */
public class LambdaMapper extends MapperWrapper {

    /**
     * Constructs a LambdaMapper.
     *
     * @param wrapped mapper
     * @since 1.4.8
     */
    public LambdaMapper(final Mapper wrapped) {
        super(wrapped);
    }

    @Override
    public String serializedClass(final Class type) {
        Class<?> replacement = null;
        if (Types.isLambdaType(type)) {
            if (Serializable.class.isAssignableFrom(type)) {
                final Class<?>[] interfaces = type.getInterfaces();
                if (interfaces.length > 1) {
                    for (int i = 0; replacement == null && i < interfaces.length; i++) {
                        final Class<?> iface = interfaces[i];
                        for (final Method method : iface.getMethods()) {
                            if (!method.isDefault() && !Modifier.isStatic(method.getModifiers())) {
                                replacement = iface;
                                break;
                            }
                        }
                    }
                } else {
                    replacement = interfaces[0];
                }
            } else {
                replacement = Null.class;
            }
        }
        return super.serializedClass(replacement == null ? type : replacement);
    }
}
