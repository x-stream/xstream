package com.thoughtworks.acceptance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.someobjects.WithNamedList;


/**
 * @author J&ouml;rg Schaible
 */
public class ConcurrencyTest extends AbstractAcceptanceTest {

    public void testConcurrentXStreaming() throws InterruptedException {
        xstream.alias("thing", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");

        final List reference = Arrays.asList(new String[]{"A", "B", "C", "D"});
        final WithNamedList[] namedLists = new WithNamedList[5];
        for (int i = 0; i < namedLists.length; ++i) {
            namedLists[i] = new WithNamedList("Name " + (i + 1));
            namedLists[i].things.add(new Software("walnes", "XStream 1." + i));
            namedLists[i].things.add(reference);
            namedLists[i].things.add(new RuntimeException("JUnit " + i)); // a Serializable
        }

        final Map exceptions = new HashMap();
        final ThreadGroup tg = new ThreadGroup(getName()) {
            public void uncaughtException(Thread t, Throwable e) {
                exceptions.put(e, t.getName());
                super.uncaughtException(t, e);
            }
        };

        final Object object = Arrays.asList(namedLists);
        final String xml = xstream.toXML(object);
        final int[] counter = new int[1];
        counter[0] = 0;
        final Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(tg, "JUnit Thread " + i) {

                public void run() {
                    int i = 0;
                    try {
                        synchronized (this) {
                            notifyAll();
                            wait();
                        }
                        while (!interrupted()) {
                            assertBothWays(object, xml);
                            ++i;
                        }
                    } catch (InterruptedException e) {
                        fail("Unexpected InterruptedException");
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
