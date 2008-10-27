/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 23. October 2008 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

/**
 * Tests Joda Time types
 * 
 * @author J&ouml;rg Schaible
 */
public class JodaTimeTypesTest extends AbstractAcceptanceTest {
    
    public void testCanHandleLocateDate() {
        DateTimeZone.setDefault(DateTimeZone.forID("America/Los_Angeles"));
        final LocalDate localDate = new LocalDate(2008, 07, 03);
        final String expected = "" +
		"<org.joda.time.LocalDate>\n" + 
		"  <iLocalMillis>1215043200000</iLocalMillis>\n" + 
		"  <iChronology class=\"org.joda.time.chrono.ISOChronology\" resolves-to=\"org.joda.time.chrono.ISOChronology$Stub\" serialization=\"custom\">\n" + 
		"    <org.joda.time.chrono.ISOChronology_-Stub>\n" + 
		"      <org.joda.time.tz.FixedDateTimeZone resolves-to=\"org.joda.time.DateTimeZone$Stub\" serialization=\"custom\">\n" + 
		"        <org.joda.time.DateTimeZone_-Stub>\n" + 
		"          <string>UTC</string>\n" + 
		"        </org.joda.time.DateTimeZone_-Stub>\n" + 
		"      </org.joda.time.tz.FixedDateTimeZone>\n" + 
		"    </org.joda.time.chrono.ISOChronology_-Stub>\n" + 
		"  </iChronology>\n" + 
		"</org.joda.time.LocalDate>";
        assertBothWays(localDate, expected);
    }
}
