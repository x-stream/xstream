package com.thoughtworks.xstream.converters.extended;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;

/**
 * A DateConverter conforming to the ISO8601 standard.
 * http://www.iso.ch/iso/en/CatalogueDetailPage.CatalogueDetail?CSNUMBER=26780
 * 
 * @author Mauro Talevi
 */
public class ISO8601DateConverter extends AbstractBasicConverter{
	private DateTimeFormatter[] formatters;
		
	public ISO8601DateConverter(){
		this.formatters = createISOFormatters();
	}
	
	public boolean canConvert(Class type) {
        return type.equals(Date.class);
    }

    protected Object fromString(String str) {
        for (int i = 0; i < formatters.length; i++) {
        		DateTimeFormatter formatter = formatters[i];
			try {
				DateTime dt = formatter.parseDateTime(str);
				return dt.toDate();
			} catch (IllegalArgumentException e) {
				// try with next formatter
			}
		}
		throw new ConversionException("Cannot parse date " + str);
    }
    
    protected String toString(Object obj) {
    		DateTime dt = new DateTime(obj);
        return dt.toString();
    }

	private DateTimeFormatter[] createISOFormatters() {
		ISODateTimeFormat isoFormat = ISODateTimeFormat.getInstance();
		List isoFormatters = new Vector();
		isoFormatters.add(isoFormat.dateTime());
		isoFormatters.add(isoFormat.dateTimeNoMillis());
		isoFormatters.add(isoFormat.basicDate());
		isoFormatters.add(isoFormat.basicDateTime());
		isoFormatters.add(isoFormat.basicDateTimeNoMillis());
		isoFormatters.add(isoFormat.basicTime());
		isoFormatters.add(isoFormat.basicTimeNoMillis());
		isoFormatters.add(isoFormat.basicTTime());
		isoFormatters.add(isoFormat.basicTTimeNoMillis());
		isoFormatters.add(isoFormat.basicWeekDate());
		isoFormatters.add(isoFormat.basicWeekDateTime());
		isoFormatters.add(isoFormat.basicWeekDateTimeNoMillis());
		isoFormatters.add(isoFormat.date());
		isoFormatters.add(isoFormat.dateHour());
		isoFormatters.add(isoFormat.dateHourMinute());
		isoFormatters.add(isoFormat.dateHourMinuteSecond());
		isoFormatters.add(isoFormat.dateHourMinuteSecondFraction());
		isoFormatters.add(isoFormat.dateHourMinuteSecondMillis());
		isoFormatters.add(isoFormat.hour());
		isoFormatters.add(isoFormat.hourMinute());
		isoFormatters.add(isoFormat.hourMinuteSecond());
		isoFormatters.add(isoFormat.hourMinuteSecondFraction());
		isoFormatters.add(isoFormat.hourMinuteSecondMillis());
		isoFormatters.add(isoFormat.time());
		isoFormatters.add(isoFormat.timeNoMillis());
		isoFormatters.add(isoFormat.tTime());
		isoFormatters.add(isoFormat.tTimeNoMillis());
		isoFormatters.add(isoFormat.weekDate());
		isoFormatters.add(isoFormat.weekDateTime());
		isoFormatters.add(isoFormat.weekDateTimeNoMillis());
		isoFormatters.add(isoFormat.weekyear());
		isoFormatters.add(isoFormat.weekyearWeek());
		isoFormatters.add(isoFormat.weekyearWeekDay());
		return (DateTimeFormatter[])isoFormatters.toArray(new DateTimeFormatter[isoFormatters.size()]);
	}
}
