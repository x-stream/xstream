/*
 * Copyright (C) 2021, 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 21. September 2021 by Joerg Schaible
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.security.InputManipulationException;


/**
 * Utility functions for security issues.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.19
 */
public class SecurityUtils {

    /**
     * Check the consumed time adding elements to collections or maps. Every custom converter should call this method
     * after an unmarshalled element has been added to a collection or map. In case of an attack the operation will take
     * too long, because the calculation of the hash code or the comparison of the elements in the collection operate on
     * recursive structures.
     *
     * @param context the unmarshalling context
     * @param start the timestamp just before the element was added to the collection or map
     * @since 1.4.19
     */
    public static void checkForCollectionDoSAttack(final UnmarshallingContext context, final long start) {
        final int diff = (int)((System.currentTimeMillis() - start) / 1000);
        if (diff > 0) {
            final Integer secondsUsed = (Integer)context.get(XStream.COLLECTION_UPDATE_SECONDS);
            if (secondsUsed != null) {
                final Integer limit = (Integer)context.get(XStream.COLLECTION_UPDATE_LIMIT);
                if (limit == null) {
                    throw new ConversionException("Missing limit for updating collections.");
                }
                final int seconds = secondsUsed.intValue() + diff;
                if (seconds > limit.intValue()) {
                    throw new InputManipulationException(
                        "Denial of Service attack assumed. Adding elements to collections or maps exceeds "
                            + limit.intValue()
                            + " seconds.");
                }
                context.put(XStream.COLLECTION_UPDATE_SECONDS, Integer.valueOf(seconds));
            }
        }
    }
}
