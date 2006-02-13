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
