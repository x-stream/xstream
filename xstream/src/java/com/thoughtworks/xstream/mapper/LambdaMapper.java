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
    public String serializedClass(final Class<?> type) {
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
