/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
        assertEquals("message             : Message", tokenizer.nextToken());
        assertEquals("1st                 : first", tokenizer.nextToken());
        assertEquals("2nd                 : second", tokenizer.nextToken());
        assertEquals("3rd                 : third", tokenizer.nextToken());
    }
}
