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

import com.thoughtworks.xstream.converters.SingleValueConverter;


/**
 * Base abstract implementation of {@link SingleValueConverter}.
 * <p>
 * Subclasses should implement methods canConvert(Class) and fromString(String) for the conversion.
 * </p>
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @see com.thoughtworks.xstream.converters.SingleValueConverter
 */
public abstract class AbstractSingleValueConverter implements SingleValueConverter {

    @Override
    public abstract boolean canConvert(Class<?> type);

    @Override
    public String toString(final Object obj) {
        return obj == null ? null : obj.toString();
    }

    @Override
    public abstract Object fromString(String str);

}
