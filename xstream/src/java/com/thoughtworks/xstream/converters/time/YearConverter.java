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

import java.time.Year;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link Year} instance to string.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class YearConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return Year.class == type;
    }

    @Override
    public Year fromString(final String str) {
        try {
            return Year.of(Integer.parseInt(str));
        } catch (final NumberFormatException ex) {
            final ConversionException exception = new ConversionException("Cannot parse value as year", ex);
            exception.add("value", str);
            throw exception;
        }
    }
}
