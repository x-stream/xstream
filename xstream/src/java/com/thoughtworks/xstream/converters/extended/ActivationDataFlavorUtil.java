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
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Helper methods to perform the common part for both converters of ActivationDataFlavor.
 *
 * @since upcoming
 */
class ActivationDataFlavorUtil {

    static class ActivationDataFlavorInfo {
        protected final String mimeType;
        protected final String name;
        protected final Class<?> representationClass;

        protected ActivationDataFlavorInfo(final String mimeType, final String name, final Class<?> representationClass) {
            this.mimeType = mimeType;
            this.name = name;
            this.representationClass = representationClass;
        }
    }

    static void doMarshal(final ActivationDataFlavorInfo info, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        if (info.mimeType != null) {
            writer.startNode("mimeType");
            writer.setValue(info.mimeType);
            writer.endNode();
        }
        if (info.name != null) {
            writer.startNode("humanRepresentableName");
            writer.setValue(info.name);
            writer.endNode();
        }
        if (info.representationClass != null) {
            writer.startNode("representationClass");
            context.convertAnother(info.representationClass);
            writer.endNode();
        }
    }

    static ActivationDataFlavorInfo doUnmarshal(final HierarchicalStreamReader reader,
            final UnmarshallingContext context) {
        String mimeType = null;
        String name = null;
        Class<?> type = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            final String elementName = reader.getNodeName();
            if (elementName.equals("mimeType")) {
                mimeType = reader.getValue();
            } else if (elementName.equals("humanRepresentableName")) {
                name = reader.getValue();
            } else if (elementName.equals("representationClass")) {
                type = (Class<?>)context.convertAnother(null, Class.class);
            } else {
                final ConversionException exception = new ConversionException("Unknown child element");
                exception.add("element", reader.getNodeName());
                throw exception;
            }
            reader.moveUp();
        }
        return new ActivationDataFlavorInfo(mimeType, name, type);
    }

}
