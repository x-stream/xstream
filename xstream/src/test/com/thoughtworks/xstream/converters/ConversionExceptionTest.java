/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. November 2006 by Joerg Schaible
 */
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
            if (tokenizer.nextToken().startsWith("---- Debugging information ----")) {
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
