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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * A converter for {@link Date} conforming to the ISO8601 standard.
 * 
 * @see <a href="http://www.iso.org/iso/home/store/catalogue_ics/catalogue_detail_ics.htm?csnumber=40874">ISO 8601</a>
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 */
public class ISO8601DateConverter extends AbstractSingleValueConverter {

    private final ISO8601GregorianCalendarConverter converter = new ISO8601GregorianCalendarConverter();

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Date.class && converter.canConvert(GregorianCalendar.class);
    }

    @Override
    public Object fromString(final String str) {
        return ((Calendar)converter.fromString(str)).getTime();
    }

    @Override
    public String toString(final Object obj) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date)obj);
        return converter.toString(calendar);
    }
}
