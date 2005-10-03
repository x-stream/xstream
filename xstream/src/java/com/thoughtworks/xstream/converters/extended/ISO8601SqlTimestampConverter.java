package com.thoughtworks.xstream.converters.extended;

import java.sql.Timestamp;
import java.util.Date;

/**
 * A SqlTimestampConverter conforming to the ISO8601 standard.
 * http://www.iso.ch/iso/en/CatalogueDetailPage.CatalogueDetail?CSNUMBER=26780
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class ISO8601SqlTimestampConverter extends ISO8601DateConverter {

    public boolean canConvert(Class type) {
        return type.equals(Timestamp.class);
    }

    protected Object fromString(String str) {
        return new Timestamp(((Date)super.fromString(str)).getTime());
    }

}
