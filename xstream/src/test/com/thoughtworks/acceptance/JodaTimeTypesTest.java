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
        xstream.allowTypesByWildcard("org.joda.time.**");
        DateTimeZone.setDefault(DateTimeZone.forID("America/Los_Angeles"));
        final LocalDate localDate = new LocalDate(2008, 07, 03);
        final String expected = ""
            + "<org.joda.time.LocalDate>\n"
            + "  <iLocalMillis>1215043200000</iLocalMillis>\n"
            + "  <iChronology class=\"org.joda.time.chrono.ISOChronology\" resolves-to=\"org.joda.time.chrono.ISOChronology$Stub\" serialization=\"custom\">\n"
            + "    <org.joda.time.chrono.ISOChronology_-Stub>\n"
            + "      <org.joda.time.UTCDateTimeZone resolves-to=\"org.joda.time.DateTimeZone$Stub\" serialization=\"custom\">\n"
            + "        <org.joda.time.DateTimeZone_-Stub>\n"
            + "          <string>UTC</string>\n"
            + "        </org.joda.time.DateTimeZone_-Stub>\n"
            + "      </org.joda.time.UTCDateTimeZone>\n"
            + "    </org.joda.time.chrono.ISOChronology_-Stub>\n"
            + "  </iChronology>\n"
            + "</org.joda.time.LocalDate>";
        assertBothWays(localDate, expected);
    }
}
