
package com.thoughtworks.xstream.converters.extended;

import java.sql.Timestamp;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.ISO8601SqlTimestampConverter;

import junit.framework.TestCase;

/**
 * Created Jan 16, 2005
 * 
 * @author Chung-Onn Cheong
 */
public class ISO8601SqlTimestampConverterTest extends TestCase {

    public void testISO8601SqlTimestamp(){
        
        XStream xs = new XStream();
        xs.registerConverter(new ISO8601SqlTimestampConverter());
        
        long currentTime = System.currentTimeMillis();
        
        Timestamp ts1 = new Timestamp(currentTime); 
        String xmlString = xs.toXML(ts1);
        System.out.println("ISO8601 xmlString:"+ xmlString);
        
        Timestamp ts2 = (Timestamp) xs.fromXML(xmlString);
        System.out.println("ISO8601 timestamp:"+ts2);
        
        assertEquals("ISO Timestamp Converted is not the same ",ts1,ts2);
        assertEquals("Current time not equal to converted timestamp", currentTime, ts2.getTime());
    }
    
}
