/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. January 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.testutil;

import java.util.TimeZone;

public class TimeZoneChanger {

    private static final TimeZone originalTimeZone = TimeZone.getDefault();

    public static void change(String timeZone) {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

    public static void reset() {
        TimeZone.setDefault(originalTimeZone);
    }

}
