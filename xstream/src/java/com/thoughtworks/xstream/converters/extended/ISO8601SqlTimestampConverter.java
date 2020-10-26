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

import java.sql.Timestamp;
import java.util.Date;


/**
 * A converter for {@link Timestamp} conforming to the ISO8601 standard.
 * 
 * @see <a href="http://www.iso.org/iso/home/store/catalogue_ics/catalogue_detail_ics.htm?csnumber=40874">ISO 8601</a>
 * @author J&ouml;rg Schaible
 * @since 1.1.3
 */
public class ISO8601SqlTimestampConverter extends ISO8601DateConverter {

    private final static String PADDING = "000000000";

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Timestamp.class && super.canConvert(Date.class);
    }

    @Override
    public Object fromString(String str) {
        final int idxFraction = str.lastIndexOf('.');
        int nanos = 0;
        if (idxFraction > 0) {
            int idx;
            for (idx = idxFraction + 1; Character.isDigit(str.charAt(idx)); ++idx) {
                ;
            }
            nanos = Integer.parseInt(str.substring(idxFraction + 1, idx));
            str = str.substring(0, idxFraction) + str.substring(idx);
        }
        final Date date = (Date)super.fromString(str);
        final Timestamp timestamp = new Timestamp(date.getTime());
        timestamp.setNanos(nanos);
        return timestamp;
    }

    @Override
    public String toString(final Object obj) {
        final Timestamp timestamp = (Timestamp)obj;
        String str = super.toString(new Date(timestamp.getTime() / 1000 * 1000));
        final String nanos = String.valueOf(timestamp.getNanos());
        final int idxFraction = str.lastIndexOf('.');
        str = str.substring(0, idxFraction + 1)
            + PADDING.substring(nanos.length())
            + nanos
            + str.substring(idxFraction + 4);
        return str;
    }

}
