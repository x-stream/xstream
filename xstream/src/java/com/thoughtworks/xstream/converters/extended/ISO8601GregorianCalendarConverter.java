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

import java.lang.reflect.InvocationTargetException;
import java.util.GregorianCalendar;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.core.JVM;


/**
 * A converter for {@link GregorianCalendar} conforming to the ISO8601 standard.
 * <p>
 * The converter will always serialize the calendar value in UTC and deserialize it to a value in the current default
 * time zone.
 * </p>
 *
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @see <a href="http://www.iso.org/iso/home/store/catalogue_ics/catalogue_detail_ics.htm?csnumber=40874">ISO 8601</a>
 * @since 1.1.3
 */
public class ISO8601GregorianCalendarConverter extends AbstractSingleValueConverter {
    private final SingleValueConverter converter;

    public ISO8601GregorianCalendarConverter() {
        SingleValueConverter svConverter = null;
        final Class<? extends SingleValueConverter> type = JVM.<SingleValueConverter>loadClassForName(JVM.isVersion(8)
            ? "com.thoughtworks.xstream.core.util.ISO8601JavaTimeConverter"
            : "com.thoughtworks.xstream.core.util.ISO8601JodaTimeConverter");
        try {
            svConverter = type.getDeclaredConstructor().newInstance();
        } catch (final
                InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | NoSuchMethodException
                | SecurityException e) {
            // ignore
        }
        converter = svConverter;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return converter != null && type == GregorianCalendar.class;
    }

    @Override
    public Object fromString(final String str) {
        return converter.fromString(str);
    }

    @Override
    public String toString(final Object obj) {
        return converter.toString(obj);
    }
}
