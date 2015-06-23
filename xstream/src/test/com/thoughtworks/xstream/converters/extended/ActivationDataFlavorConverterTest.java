/*
 * Copyright (C) 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 23.06.2015 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.awt.datatransfer.DataFlavor;

import javax.activation.ActivationDataFlavor;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;


public class ActivationDataFlavorConverterTest extends AbstractAcceptanceTest {

    protected void setupSecurity(XStream xstream) {
        super.setupSecurity(xstream);
        xstream.allowTypeHierarchy(DataFlavor.class);
    }

    public void testMimeTypeOnly() {
        final String expected = ""
            + "<activation-data-flavor>\n"
            + "  <mimeType>application/x-junit</mimeType>\n"
            + "  <representationClass>java.io.InputStream</representationClass>\n"
            + "</activation-data-flavor>";
        assertBothWays(new ActivationDataFlavor("application/x-junit", null), expected);
    }

    public void testMimeTypeAndRepresentation() {
        final String expected = ""
            + "<activation-data-flavor>\n"
            + "  <mimeType>application/x-junit</mimeType>\n"
            + "  <humanRepresentableName>JUnit</humanRepresentableName>\n"
            + "  <representationClass>java.io.InputStream</representationClass>\n"
            + "</activation-data-flavor>";
        assertBothWays(new ActivationDataFlavor("application/x-junit", "JUnit"), expected);
    }

    public void testWithAllArguments() {
        final String expected = ""
            + "<activation-data-flavor>\n"
            + "  <mimeType>application/x-junit</mimeType>\n"
            + "  <humanRepresentableName>JUnit</humanRepresentableName>\n"
            + "  <representationClass>com.thoughtworks.xstream.converters.extended.ActivationDataFlavorConverterTest</representationClass>\n"
            + "</activation-data-flavor>";
        assertBothWays(new ActivationDataFlavor(ActivationDataFlavorConverterTest.class, "application/x-junit", "JUnit"), expected);
    }
}
