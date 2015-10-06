/*
 * Copyright (C) 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. September 2015 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import java.io.StringReader;

public class StandardStaxReaderTest extends AbstractXMLReaderTest {

    public static Test suite() {
        if (JVM.is16()) {
            return new TestSuite(StandardStaxReaderTest.class);
        } else {
            return new TestCase(StandardStaxReaderTest.class.getName() + ": not available") {
        
                public int countTestCases() {
                    return 1;
                }
        
                public void run(TestResult result) {
                }
            };
        }
    }

    private HierarchicalStreamDriver driver = new StandardStaxDriver();

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        return driver.createReader(new StringReader(xml));
    }

    public void testIsXXEVulnerableWithExternalParameterEntity() throws Exception {
        // fails for Sun JDK 1.6 runtime
        if (JVM.is17() || !JVM.getStaxInputFactory().getName().equals("com.sun.xml.internal.stream.XMLInputFactoryImpl")) {
            super.testIsXXEVulnerableWithExternalParameterEntity();
        }
    }

    // inherits tests from superclass
}
