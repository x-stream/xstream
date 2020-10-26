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

package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a long primitive or {@link Long} wrapper to a string.
 * 
 * @author Joe Walnes
 */
public class LongConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == long.class || type == Long.class;
    }

    @Override
    public Object fromString(final String str) {
        final int len = str.length();
        if (len == 0) {
            throw new NumberFormatException("For input string: \"\"");
        }
        if (len < 17) {
            return Long.decode(str);
        }
        final char c0 = str.charAt(0);
        if (c0 != '0' && c0 != '#') {
            return Long.decode(str);
        }
        final char c1 = str.charAt(1);
        final long high;
        final long low;
        if (c0 == '#' && len == 17) {
            high = Long.parseLong(str.substring(1, 9), 16) << 32;
            low = Long.parseLong(str.substring(9, 17), 16);
        } else if ((c1 == 'x' || c1 == 'X') && len == 18) {
            high = Long.parseLong(str.substring(2, 10), 16) << 32;
            low = Long.parseLong(str.substring(10, 18), 16);
        } else if (len == 23 && c1 == '1') {
            high = Long.parseLong(str.substring(1, 12), 8) << 33;
            low = Long.parseLong(str.substring(12, 23), 8);
        } else {
            return Long.decode(str);
        }
        final long num = high | low;
        return Long.valueOf(num);
    }

}
