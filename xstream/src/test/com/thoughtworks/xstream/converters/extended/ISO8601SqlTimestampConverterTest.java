
package com.thoughtworks.xstream.converters.extended;

import java.sql.Timestamp;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.ISO8601SqlTimestampConverter;

import junit.framework.TestCase;

/**
 * @author Chung-Onn Cheong
 * @author J&ouml;rg Schaible
 */
public class ISO8601SqlTimestampConverterTest extends TestCase {

    public void testISO8601SqlTimestamp(){
        
        XStream xs = new XStream();
        xs.registerConverter(new ISO8601SqlTimestampConverter());
        
        long currentTime = System.currentTimeMillis();
        
        Timestamp ts1 = new Timestamp(currentTime); 
        String xmlString = xs.toXML(ts1);
        
        Timestamp ts2 = (Timestamp) xs.fromXML(xmlString);
        
        assertEquals("ISO Timestamp Converted is not the same ",ts1,ts2);
        assertEquals("Current time not equal to converted timestamp", currentTime, ts2.getTime() / 1000 * 1000 + ts2.getNanos() / 1000000);
    }
    
}
