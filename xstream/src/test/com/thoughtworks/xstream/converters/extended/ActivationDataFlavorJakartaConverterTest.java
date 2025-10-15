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

import jakarta.activation.ActivationDataFlavor;

import com.thoughtworks.xstream.XStream;


public class ActivationDataFlavorJakartaConverterTest extends AbstractActivationDataFlavorConverterTest {

    protected void setupSecurity(XStream xstream) {
        super.setupSecurity(xstream);
        xstream.allowTypeHierarchy(ActivationDataFlavor.class);
    }

    protected Object newActivationDataFlavor(String mimeType, String humanPresentableName, Class type) {
        return type == null
            ? new ActivationDataFlavor(mimeType, humanPresentableName)
            : new ActivationDataFlavor(type, mimeType, humanPresentableName);
    }
}
