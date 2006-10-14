package com.thoughtworks.xstream.core.util;

import junit.framework.TestCase;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author J&ouml;rg Schaible
 */
public class ThreadSafeSimpleDateFormatTest extends TestCase {
    public void testConcurrentDateFormatting() throws InterruptedException {

        final ThreadSafeSimpleDateFormat format = new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss,S z", 2, 4);
        final Date now = new Date();
        
        final Map exceptions = new HashMap();
        final ThreadGroup tg = new ThreadGroup(getName()) {
            public void uncaughtException(Thread t, Throwable e) {
                exceptions.put(e, t.getName());
                super.uncaughtException(t, e);
            }
        };

        final int[] counter = new int[1];
        counter[0] = 0;
        final Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(tg, "JUnit Thread " + i) {

                public void run() {
                    int i = 0;
                    try {
                        synchronized (this) {
                            notifyAll();
                            wait();
                        }
                        while (i < 1000  && !interrupted()) {
                            String formatted = format.format(now);
                            Thread.yield();
                            assertEquals(now, format.parse(formatted));
                            ++i;
                        }
                    } catch (InterruptedException e) {
                        fail("Unexpected InterruptedException");
                    } catch (ParseException e) {
                        fail("Unexpected ParseException");
                    }
                    synchronized (counter) {
                        counter[0] += i;
                    }
                }

            };
        }

        for (int i = 0; i < threads.length; ++i) {
            synchronized (threads[i]) {
                threads[i].start();
                threads[i].wait();
            }
        }

        for (int i = 0; i < threads.length; ++i) {
            synchronized (threads[i]) {
                threads[i].notifyAll();
            }
        }

        Thread.sleep(1000);

        for (int i = 0; i < threads.length; ++i) {
            threads[i].interrupt();
        }
        for (int i = 0; i < threads.length; ++i) {
            synchronized (threads[i]) {
                threads[i].join();
            }
        }

        assertEquals("Exceptions has been thrown: " + exceptions, 0, exceptions.size());
        assertTrue("Each thread should have made at least 1 conversion", counter[0] >= threads.length);
    }

}
