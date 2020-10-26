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

package com.thoughtworks.xstream.converters.extended;

import java.io.NotSerializableException;

import javax.swing.LookAndFeel;

import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * A converter for Swing {@link LookAndFeel} implementations.
 * <p>
 * The JDK's implementations are serializable for historical reasons but will throw a {@link NotSerializableException}
 * in their writeObject method. Therefore XStream will use an implementation based on the ReflectionConverter.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class LookAndFeelConverter extends ReflectionConverter {

    /**
     * Constructs a LookAndFeelConverter.
     * 
     * @param mapper the mapper
     * @param reflectionProvider the reflection provider
     * @since 1.3
     */
    public LookAndFeelConverter(final Mapper mapper, final ReflectionProvider reflectionProvider) {
        super(mapper, reflectionProvider);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && LookAndFeel.class.isAssignableFrom(type) && canAccess(type);
    }
}
