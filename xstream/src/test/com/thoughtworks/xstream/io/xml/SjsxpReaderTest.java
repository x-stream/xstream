/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. September 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import java.io.StringReader;

public class SjsxpReaderTest extends AbstractXMLReaderTest {
    final static String className = "com.sun.xml.internal.stream.XMLInputFactoryImpl";

    public static Test suite() {
        try {
            Class.forName(className);
            return new TestSuite(SjsxpReaderTest.class);
        } catch (ClassNotFoundException e) {
            return new TestCase(SjsxpReaderTest.class.getName() + ": not available") {
        
                public int countTestCases() {
                    return 1;
                }
        
                public void run(TestResult result) {
                }
            };
        }
    }
    
    private HierarchicalStreamDriver driver = new SjsxpDriver();

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        return driver.createReader(new StringReader(xml));
    }

    // inherits tests from superclass
}
