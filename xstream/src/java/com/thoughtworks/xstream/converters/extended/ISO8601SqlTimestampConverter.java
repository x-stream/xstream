package com.thoughtworks.xstream.converters.extended;

import java.sql.Timestamp;
import java.util.Date;


/**
 * A SqlTimestampConverter conforming to the ISO8601 standard.
 * http://www.iso.ch/iso/en/CatalogueDetailPage.CatalogueDetail?CSNUMBER=26780
 * <p>
 * Note, that this converter cannot handle the nano and micro part of the Timestamp.
 * Any value is truncated at milliseconds level.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class ISO8601SqlTimestampConverter extends ISO8601DateConverter {

    public boolean canConvert(Class type) {
        return type.equals(Timestamp.class);
    }

    protected Object fromString(String str) {
        // special nano handling required for JDK 1.3
        final Date date = (Date)super.fromString(str);
        final Timestamp timestamp = new Timestamp((date.getTime() / 1000) * 1000);
        timestamp.setNanos((int)((date.getTime() % 1000) * 1000000));
        return timestamp;
    }

    protected String toString(Object obj) {
        // special nano handling required for JDK 1.3
        final Timestamp timestamp = (Timestamp)obj;
        return super.toString(new Date(((timestamp.getTime() / 1000) * 1000)
                + (timestamp.getNanos() / 1000000)));
    }

}
