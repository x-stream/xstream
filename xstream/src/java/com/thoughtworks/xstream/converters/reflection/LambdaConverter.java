/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
    public boolean canConvert(final Class<?> type) {
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
