package com.thoughtworks.acceptance;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import com.thoughtworks.acceptance.objects.Software;

public class ThreadSafety extends AbstractAcceptanceTest {

    public void testHammeringFromDifferentThreads() throws Exception {

        final Object input = new Software("apache", "ant");
        
        List threads = new ArrayList();
        
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread() {
                public void run() {
                    for (int j = 0; j < 20; j++) {
                        String xml = xstream.toXML(input);
                        Object result = xstream.fromXML(xml);
                        assertEquals(input, result);
                    }
                }
            };
            threads.add(thread);
            thread.start();
        }
        
        for (Iterator iter = threads.iterator(); iter.hasNext();) {
            Thread thread = (Thread) iter.next();
            thread.join();
        }
        
    }
}
