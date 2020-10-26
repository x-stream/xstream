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

import java.util.Locale;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link Locale} to a string.
 * 
 * @author Jose A. Illescas
 * @author Joe Walnes
 */
public class LocaleConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Locale.class;
    }

    @Override
    public Object fromString(final String str) {
        final int[] underscorePositions = underscorePositions(str);
        String language, country, variant;
        if (underscorePositions[0] == -1) { // "language"
            language = str;
            country = "";
            variant = "";
        } else if (underscorePositions[1] == -1) { // "language_country"
            language = str.substring(0, underscorePositions[0]);
            country = str.substring(underscorePositions[0] + 1);
            variant = "";
        } else { // "language_country_variant"
            language = str.substring(0, underscorePositions[0]);
            country = str.substring(underscorePositions[0] + 1, underscorePositions[1]);
            variant = str.substring(underscorePositions[1] + 1);
        }
        return new Locale(language, country, variant);
    }

    private int[] underscorePositions(final String in) {
        final int[] result = new int[2];
        for (int i = 0; i < result.length; i++) {
            final int last = i == 0 ? 0 : result[i - 1];
            result[i] = in.indexOf('_', last + 1);
        }
        return result;
    }

}
