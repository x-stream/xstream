/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 23. November 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;


/**
 * Tests annotation to omit a field.
 * 
 * @author Chung-Onn Cheong
 * @author Mauro Talevi
 * @author Guilherme Silveira
 * @author J&ouml;rg Schaible
 */
public class OmitFieldTest extends AbstractAcceptanceTest {
    
    @Override
    protected XStream createXStream() {
        XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    @XStreamAlias("apartment")
    public static class Apartment {

        @XStreamOmitField
        int size;

        protected Apartment(int size) {
            this.size = size;
        }
    }

    public void testAnnotation() {
        Apartment ap = new Apartment(5);
        String expectedXml = "<apartment/>";
        assertBothWays(ap, expectedXml);
    }
}
