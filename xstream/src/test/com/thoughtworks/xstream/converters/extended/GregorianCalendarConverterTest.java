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

import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author J&ouml;rg Schaible
 */
public class GregorianCalendarConverterTest extends TestCase {

    public void testCalendar() {
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        final XStream xstream = new XStream();
        final String xml = xstream.toXML(cal);
        final Calendar serialized = xstream.<Calendar>fromXML(xml);
        assertEquals(cal, serialized);
    }

}
