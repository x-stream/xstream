package com.thoughtworks.xstream.converters.extended;

import java.util.Calendar;
import java.util.Date;


/**
 * A DateConverter conforming to the ISO8601 standard.
 * http://www.iso.ch/iso/en/CatalogueDetailPage.CatalogueDetail?CSNUMBER=26780
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 */
public class ISO8601DateConverter extends ISO8601GregorianCalendarConverter {

    public boolean canConvert(Class type) {
        return type.equals(Date.class);
    }

    public Object fromString(String str) {
        return ((Calendar)super.fromString(str)).getTime();
    }

    public String toString(Object obj) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date)obj);
        return super.toString(calendar);
    }
}
