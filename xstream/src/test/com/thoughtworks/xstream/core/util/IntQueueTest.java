package com.thoughtworks.xstream.core.util;

import junit.framework.TestCase;

public class IntQueueTest extends TestCase {

    public void testReadsInSameOrderItemsWereWritten() {
        IntQueue q = new IntQueue(4);

        assertTrue(q.isEmpty());

        q.write(10);
        q.write(20);
        q.write(30);

        assertFalse(q.isEmpty());
        assertEquals(10, q.read());
        assertEquals(20, q.read());
        assertFalse(q.isEmpty());

        q.write(40);
        q.write(50);
        q.write(60);

        assertFalse(q.isEmpty());
        assertEquals(30, q.read());
        assertEquals(40, q.read());
        assertEquals(50, q.read());
        assertFalse(q.isEmpty());
        assertEquals(60, q.read());
        assertTrue(q.isEmpty());
    }

    public void testThrowsExceptionIfSizeExceeded() {
        IntQueue q = new IntQueue(4);
        q.write(1);
        q.write(2);
        q.write(3);
        q.write(4);
        try {
            q.write(5);
            fail("Expected exeption");
        } catch (IntQueue.OverflowException expectedException) {
            // good
        }
    }

    public void testThrowsExceptionWhenReadingNothing() {
        IntQueue q = new IntQueue(4);

        try {
            q.read();
            fail("Expected exeption");
        } catch (IntQueue.NothingToReadException expectedException) {
            // good
        }

        q.write(1);
        q.write(2);

        q.read();
        q.read();

        try {
            q.read();
            fail("Expected exeption");
        } catch (IntQueue.NothingToReadException expectedException) {
            // good
        }

    }
}
