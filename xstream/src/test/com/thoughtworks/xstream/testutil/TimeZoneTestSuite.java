package com.thoughtworks.xstream.testutil;

import junit.extensions.TestDecorator;
import junit.framework.Test;
import junit.framework.TestResult;

import java.util.TimeZone;

/**
 * Wraps a JUnit test (or suite), ensuring that it runs in a particular timezone.
 *
 * @author Joe Walnes
 */
public class TimeZoneTestSuite extends TestDecorator {

    private final TimeZone timeZone;
    private final TimeZone originalTimeZone;

    public TimeZoneTestSuite(String timeZone, Test test) {
        super(test);
        this.timeZone = TimeZone.getTimeZone(timeZone);
        originalTimeZone = TimeZone.getDefault();
    }

    public void run(TestResult testResult) {
        try {
            TimeZone.setDefault(timeZone);
            super.run(testResult);
        } finally {
            TimeZone.setDefault(originalTimeZone); // cleanup
        }
    }

}
