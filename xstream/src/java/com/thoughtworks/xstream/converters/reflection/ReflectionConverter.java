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

import com.thoughtworks.xstream.mapper.Mapper;


public class ReflectionConverter extends AbstractReflectionConverter {

    private Class<?> type;

    public ReflectionConverter(final Mapper mapper, final ReflectionProvider reflectionProvider) {
        super(mapper, reflectionProvider);
    }

    /**
     * Construct a ReflectionConverter for an explicit type.
     * 
     * @param mapper the mapper in use
     * @param reflectionProvider the reflection provider in use
     * @param type the explicit type to handle
     * @since 1.4.7
     */
    public ReflectionConverter(final Mapper mapper, final ReflectionProvider reflectionProvider, final Class<?> type) {
        this(mapper, reflectionProvider);
        this.type = type;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return (this.type != null && this.type == type || this.type == null && type != null) && canAccess(type);
    }
}
