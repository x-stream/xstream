package com.thoughtworks.xstream.converters.reference;

import junit.framework.TestCase;

public class CircularityTrackerTest extends TestCase {
    private CircularityTracker circularityTracker;

    protected void setUp() throws Exception {
        super.setUp();
        circularityTracker = new CircularityTracker();
    }

    public void testThrowsExceptionIfSameObjectIsTrackedTwice() {
        Object value = new Object();
        circularityTracker.track(value);
        try {
            circularityTracker.track(value);
            fail("expected exception");
        } catch (CircularityException shouldHappen) {
        }
    }

    public void testAllowsObjectsThatAreEqualButNotTheSame() {
        circularityTracker.track(new AlwaysEquals());
        circularityTracker.track(new AlwaysEquals());
    }

    class AlwaysEquals {
        public boolean equals(Object obj) {
            return true;
        }
    }
}
