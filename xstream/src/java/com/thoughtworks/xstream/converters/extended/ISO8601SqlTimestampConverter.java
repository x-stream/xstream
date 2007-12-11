/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. October 2005 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.sql.Timestamp;
import java.util.Date;


/**
 * A SqlTimestampConverter conforming to the ISO8601 standard.
 * http://www.iso.ch/iso/en/CatalogueDetailPage.CatalogueDetail?CSNUMBER=26780
 * 
 * @author J&ouml;rg Schaible
 * @since 1.1.3
 */
public class ISO8601SqlTimestampConverter extends ISO8601DateConverter {

    private final static String PADDING = "000000000";

    public boolean canConvert(Class type) {
        return type.equals(Timestamp.class);
    }

    public Object fromString(String str) {
        final int idxFraction = str.lastIndexOf('.');
        int nanos = 0;
        if (idxFraction > 0) {
            int idx;
            for (idx = idxFraction + 1; Character.isDigit(str.charAt(idx)); ++idx)
                ;
            nanos = Integer.parseInt(str.substring(idxFraction + 1, idx));
            str = str.substring(0, idxFraction) + str.substring(idx);
        }
        final Date date = (Date)super.fromString(str);
        final Timestamp timestamp = new Timestamp(date.getTime());
        timestamp.setNanos(nanos);
        return timestamp;
    }

    public String toString(Object obj) {
        final Timestamp timestamp = (Timestamp)obj;
        String str = super.toString(new Date((timestamp.getTime() / 1000) * 1000));
        final String nanos = String.valueOf(timestamp.getNanos());
        final int idxFraction = str.lastIndexOf('.');
        str = str.substring(0, idxFraction + 1)
                + PADDING.substring(nanos.length())
                + nanos
                + str.substring(idxFraction + 4);
        return str;
    }

}
