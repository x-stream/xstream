/*
 * Copyright (C) 2025 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 15. October 2025 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;


abstract class AbstractActivationDataFlavorConverterTest extends AbstractAcceptanceTest {
    
    protected abstract Object newActivationDataFlavor(String mimeType, String humanPresentableName, Class type);

    public void testMimeTypeOnly() {
        final String expected = ""
            + "<activation-data-flavor>\n"
            + "  <mimeType>application/x-junit</mimeType>\n"
            + "  <representationClass>java.io.InputStream</representationClass>\n"
            + "</activation-data-flavor>";
        assertBothWays(newActivationDataFlavor("application/x-junit", null, null), expected);
    }

    public void testMimeTypeAndRepresentation() {
        final String expected = ""
            + "<activation-data-flavor>\n"
            + "  <mimeType>application/x-junit</mimeType>\n"
            + "  <humanRepresentableName>JUnit</humanRepresentableName>\n"
            + "  <representationClass>java.io.InputStream</representationClass>\n"
            + "</activation-data-flavor>";
        assertBothWays(newActivationDataFlavor("application/x-junit", "JUnit", null), expected);
    }

    public void testWithAllArguments() {
        final String expected = ""
            + "<activation-data-flavor>\n"
            + "  <mimeType>application/x-junit</mimeType>\n"
            + "  <humanRepresentableName>JUnit</humanRepresentableName>\n"
            + "  <representationClass>com.thoughtworks.xstream.converters.extended.ActivationDataFlavorConverterTest</representationClass>\n"
            + "</activation-data-flavor>";
        assertBothWays(newActivationDataFlavor("application/x-junit", "JUnit", ActivationDataFlavorConverterTest.class),
            expected);
    }
}
