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
 * Converts a boolean primitive or {@link Boolean} wrapper to a string.
 * 
 * @author Joe Walnes
 * @author David Blevins
 */
public class BooleanConverter extends AbstractSingleValueConverter {

    public static final BooleanConverter TRUE_FALSE = new BooleanConverter("true", "false", false);
    public static final BooleanConverter YES_NO = new BooleanConverter("yes", "no", false);
    public static final BooleanConverter BINARY = new BooleanConverter("1", "0", true);

    private final String positive;
    private final String negative;
    private final boolean caseSensitive;

    public BooleanConverter(final String positive, final String negative, final boolean caseSensitive) {
        this.positive = positive;
        this.negative = negative;
        this.caseSensitive = caseSensitive;
    }

    public BooleanConverter() {
        this("true", "false", false);
    }

    /**
     * @deprecated As of 1.4.8 use {@link #canConvert(Class)}
     */
    @Deprecated
    public boolean shouldConvert(final Class<?> type, final Object value) {
        return true;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == boolean.class || type == Boolean.class;
    }

    @Override
    public Object fromString(final String str) {
        if (caseSensitive) {
            return positive.equals(str) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            return positive.equalsIgnoreCase(str) ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    @Override
    public String toString(final Object obj) {
        final Boolean value = (Boolean)obj;
        return obj == null ? null : value.booleanValue() ? positive : negative;
    }
}
