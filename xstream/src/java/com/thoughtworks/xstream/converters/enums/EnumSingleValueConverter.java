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

package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * A single value converter for a special enum type. Converter is internally automatically instantiated for enum types.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class EnumSingleValueConverter<T extends Enum<T>> extends AbstractSingleValueConverter {

    private final Class<T> enumType;

    public EnumSingleValueConverter(final Class<T> type) {
        if (!Enum.class.isAssignableFrom(type) && !Enum.class.equals(type)) {
            throw new IllegalArgumentException("Converter can only handle defined enums");
        }
        enumType = type;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && enumType.isAssignableFrom(type);
    }

    @Override
    public String toString(final Object obj) {
        return Enum.class.cast(obj).name();
    }

    @Override
    public Object fromString(final String str) {
        return Enum.valueOf(enumType, str);
    }
}
