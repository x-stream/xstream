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

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.EnumMapper;
import com.thoughtworks.xstream.mapper.Mapper;


class UseAttributeForEnumMapper extends AttributeMapper {

    public UseAttributeForEnumMapper(final Mapper wrapped) {
        super(wrapped, null, null);
    }

    @Override
    public boolean shouldLookForSingleValueConverter(final String fieldName, final Class<?> type,
            final Class<?> definedIn) {
        return Enum.class.isAssignableFrom(type);
    }

    @Override
    public SingleValueConverter getConverterFromItemType(final String fieldName, final Class<?> type,
            final Class<?> definedIn) {
        return null;
    }

    @Override
    public SingleValueConverter getConverterFromAttribute(final Class<?> definedIn, final String attribute,
            final Class<?> type) {
        return null;
    }

    static Mapper createEnumMapper(final Mapper mapper) {
        return new EnumMapper(new UseAttributeForEnumMapper(mapper.lookupMapperOfType(DefaultMapper.class)));
    }
}
