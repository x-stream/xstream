package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * A GregorianCalendarConverter conforming to the ISO8601 standard.
 * http://www.iso.ch/iso/en/CatalogueDetailPage.CatalogueDetail?CSNUMBER=26780
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @since 1.1.3
 */
public class ISO8601GregorianCalendarConverter extends AbstractSingleValueConverter {
    private static final DateTimeFormatter[] formattersUTC = new DateTimeFormatter[]{
            ISODateTimeFormat.dateTime(), 
            ISODateTimeFormat.dateTimeNoMillis(),
            ISODateTimeFormat.basicDateTime(), 
            ISODateTimeFormat.basicOrdinalDateTime(),
            ISODateTimeFormat.basicOrdinalDateTimeNoMillis(), 
            ISODateTimeFormat.basicTime(),
            ISODateTimeFormat.basicTimeNoMillis(), 
            ISODateTimeFormat.basicTTime(),
            ISODateTimeFormat.basicTTimeNoMillis(), 
            ISODateTimeFormat.basicWeekDateTime(),
            ISODateTimeFormat.basicWeekDateTimeNoMillis(), 
            ISODateTimeFormat.ordinalDateTime(),
            ISODateTimeFormat.ordinalDateTimeNoMillis(), 
            ISODateTimeFormat.time(),
            ISODateTimeFormat.timeNoMillis(), 
            ISODateTimeFormat.tTime(),
            ISODateTimeFormat.tTimeNoMillis(), 
            ISODateTimeFormat.weekDateTime(),
            ISODateTimeFormat.weekDateTimeNoMillis(),};
    private static final DateTimeFormatter[] formattersNoUTC = new DateTimeFormatter[]{
            ISODateTimeFormat.basicDate(), 
            ISODateTimeFormat.basicOrdinalDate(),
            ISODateTimeFormat.basicWeekDate(), 
            ISODateTimeFormat.date(),
            ISODateTimeFormat.dateHour(), 
            ISODateTimeFormat.dateHourMinute(),
            ISODateTimeFormat.dateHourMinuteSecond(),
            ISODateTimeFormat.dateHourMinuteSecondFraction(),
            ISODateTimeFormat.dateHourMinuteSecondMillis(), 
            ISODateTimeFormat.hour(),
            ISODateTimeFormat.hourMinute(), 
            ISODateTimeFormat.hourMinuteSecond(),
            ISODateTimeFormat.hourMinuteSecondFraction(),
            ISODateTimeFormat.hourMinuteSecondMillis(), 
            ISODateTimeFormat.ordinalDate(),
            ISODateTimeFormat.weekDate(), 
            ISODateTimeFormat.year(), 
            ISODateTimeFormat.yearMonth(),
            ISODateTimeFormat.yearMonthDay(), 
            ISODateTimeFormat.weekyear(),
            ISODateTimeFormat.weekyearWeek(), 
            ISODateTimeFormat.weekyearWeekDay(),};

    public boolean canConvert(Class type) {
        return type.equals(GregorianCalendar.class);
    }

    public Object fromString(String str) {
        for (int i = 0; i < formattersUTC.length; i++) {
            DateTimeFormatter formatter = formattersUTC[i];
            try {
                DateTime dt = formatter.parseDateTime(str);
                Calendar calendar = dt.toCalendar(Locale.getDefault());
                calendar.setTimeZone(TimeZone.getDefault());
                return calendar;
            } catch (IllegalArgumentException e) {
                // try with next formatter
            }
        }
        String timeZoneID = TimeZone.getDefault().getID();
        for (int i = 0; i < formattersNoUTC.length; i++) {
            try {
                DateTimeFormatter formatter = formattersNoUTC[i].withZone(DateTimeZone.forID(timeZoneID));
                DateTime dt = formatter.parseDateTime(str);
                Calendar calendar = dt.toCalendar(Locale.getDefault());
                calendar.setTimeZone(TimeZone.getDefault());
                return calendar;
            } catch (IllegalArgumentException e) {
                // try with next formatter
            }
        }
        throw new ConversionException("Cannot parse date " + str);
    }

    public String toString(Object obj) {
        DateTime dt = new DateTime(obj);
        return dt.toString(formattersUTC[0]);
    }
}
