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

import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converts a {@link GregorianCalendar}.
 * <p>
 * Note that although it currently only contains one field, it nests it inside a child element, to allow for other
 * fields to be stored in the future.
 * </p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class GregorianCalendarConverter implements Converter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == GregorianCalendar.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final GregorianCalendar calendar = (GregorianCalendar)source;
        writer.startNode("time", long.class);
        final long timeInMillis = calendar.getTimeInMillis();
        writer.setValue(String.valueOf(timeInMillis));
        writer.endNode();
        writer.startNode("timezone", String.class);
        writer.setValue(calendar.getTimeZone().getID());
        writer.endNode();
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        reader.moveDown();
        final long timeInMillis = Long.parseLong(reader.getValue());
        reader.moveUp();
        final String timeZone;
        if (reader.hasMoreChildren()) {
            reader.moveDown();
            timeZone = reader.getValue();
            reader.moveUp();
        } else { // backward compatibility to XStream 1.1.2 and below
            timeZone = TimeZone.getDefault().getID();
        }

        final GregorianCalendar result = new GregorianCalendar();
        result.setTimeZone(TimeZone.getTimeZone(timeZone));
        result.setTimeInMillis(timeInMillis);

        return result;
    }

}
