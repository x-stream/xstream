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

package com.thoughtworks.xstream.converters.time;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;


/**
 * Converts a {@link ZoneId} instance to string.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class ZoneIdConverter implements SingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && ZoneId.class.isAssignableFrom(type);
    }

    @Override
    public ZoneId fromString(final String str) {
        ConversionException exception;
        try {
            return ZoneId.of(str);
        } catch (final ZoneRulesException e) {
            exception = new ConversionException("Not a valid zone id", e);
        } catch (final DateTimeException e) {
            exception = new ConversionException("Cannot parse value as zone id", e);
        }
        exception.add("value", str);
        throw exception;
    }

    @Override
    public String toString(final Object obj) {
        if (obj == null) {
            return null;
        }
        final ZoneId zoneId = (ZoneId)obj;
        return zoneId.getId();
    }
}
