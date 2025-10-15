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

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import jakarta.activation.ActivationDataFlavor;


/**
 * Converts an {@link ActivationDataFlavor}.
 *
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class ActivationDataFlavorJakartaConverter implements Converter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == ActivationDataFlavor.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final ActivationDataFlavor dataFlavor = (ActivationDataFlavor)source;
        final String mimeType = dataFlavor.getMimeType();
        final String name = dataFlavor.getHumanPresentableName();
        final Class<?> representationClass = dataFlavor.getRepresentationClass();
        ActivationDataFlavorUtil.doMarshal(new ActivationDataFlavorUtil.ActivationDataFlavorInfo(mimeType, name,
            representationClass), writer, context);
    }

    @Override
    public ActivationDataFlavor unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final ActivationDataFlavorUtil.ActivationDataFlavorInfo info = ActivationDataFlavorUtil.doUnmarshal(reader,
            context);
        ActivationDataFlavor dataFlavor = null;
        try {
            if (info.representationClass == null) {
                dataFlavor = new ActivationDataFlavor(info.mimeType, info.name);
            } else if (info.mimeType == null) {
                dataFlavor = new ActivationDataFlavor(info.representationClass, info.name);
            } else {
                dataFlavor = new ActivationDataFlavor(info.representationClass, info.mimeType, info.name);
            }
        } catch (final IllegalArgumentException | NullPointerException ex) {
            throw new ConversionException(ex);
        }
        return dataFlavor;
    }
}
