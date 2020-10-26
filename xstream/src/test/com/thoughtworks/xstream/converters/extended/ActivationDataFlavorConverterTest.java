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

package com.thoughtworks.xstream.converters.extended;

import java.awt.datatransfer.DataFlavor;

import javax.activation.ActivationDataFlavor;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;


public class ActivationDataFlavorConverterTest extends AbstractAcceptanceTest {

    @Override
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
