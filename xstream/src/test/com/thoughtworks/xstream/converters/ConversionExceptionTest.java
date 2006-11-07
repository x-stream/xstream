package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.mapper.CannotResolveClassException;

import junit.framework.TestCase;

import java.util.StringTokenizer;

/**
 * @author J&ouml;rg Schaible
 */
public class ConversionExceptionTest extends TestCase {

    public void testDebugMessageIsNotNested() {
        Exception ex = new CannotResolveClassException("JUnit");
        ConversionException innerEx = new ConversionException("Inner", ex);
        ConversionException outerEx = new ConversionException("Outer", innerEx);
        StringTokenizer tokenizer = new StringTokenizer(outerEx.getMessage(), "\n\r");
        int ends = 0;
        while(tokenizer.hasMoreTokens()) {
            if (tokenizer.nextToken().startsWith("----------")) {
                ++ends;
            }
        }
        assertEquals(1, ends);
    }
    
    public void testInfoRetainsOrder() {
        ConversionException ex = new ConversionException("Message");
        ex.add("1st", "first");
        ex.add("2nd", "second");
        ex.add("3rd", "third");
        StringTokenizer tokenizer = new StringTokenizer(ex.getMessage(), "\n\r");
        tokenizer.nextToken();
        tokenizer.nextToken();
        assertEquals("1st                 : first", tokenizer.nextToken());
        assertEquals("2nd                 : second", tokenizer.nextToken());
        assertEquals("3rd                 : third", tokenizer.nextToken());
    }
}
