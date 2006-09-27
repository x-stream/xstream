package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.alias.ClassMapper;

/**
 * Test XStream 1.1 compatibility.
 * 
 * @author J&ouml;rg Schaible
 */
public class XStream11CompatibilityTest extends AbstractAcceptanceTest {

    public void testClassMapperCompatibility() {
        ClassMapper mapper = xstream.getClassMapper();
        assertEquals("string", mapper.serializedClass(String.class));
    }
}
