package com.thoughtworks.xstream.io.naming;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


public class StaticNamecoderTest extends TestCase {

	private static final String TESTKEY = "name";
	private static final String TESTVALUE = "attribute";

	private StaticNameCoder staticNameCoder;
	private Map<String, String> java2Node;
	private Map<String, String> java2Attribute;

    protected void setUp() throws Exception {
        super.setUp();
        
        java2Node = new HashMap<>();
        java2Attribute = new HashMap<>();
        
        staticNameCoder = new StaticNameCoder(java2Node, java2Attribute);
    }
    
    public void testDecodeAttribute() {
    	java2Attribute.put(TESTKEY, TESTVALUE);
    	
    	final String actualKey = staticNameCoder.decodeAttribute(TESTVALUE);
    	assertEquals(TESTVALUE, actualKey);
    	
    }
}
