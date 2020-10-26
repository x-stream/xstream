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

import java.util.Currency;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link Currency} to a string.
 * <p>
 * Despite the name of this class, it has nothing to do with converting currencies between exchange rates! It makes
 * sense in the context of XStream.
 * </p>
 * 
 * @author Jose A. Illescas
 * @author Joe Walnes
 */
public class CurrencyConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Currency.class;
    }

    @Override
    public Object fromString(final String str) {
        return Currency.getInstance(str);
    }
}
