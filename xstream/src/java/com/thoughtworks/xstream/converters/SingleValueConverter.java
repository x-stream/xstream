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

package com.thoughtworks.xstream.converters;

/**
 * SingleValueConverter implementations are marshallable to/from a single value String representation.
 *
 * <p>{@link com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter}
 * provides a starting point for objects that can store all information in a single value String.</p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @see com.thoughtworks.xstream.converters.Converter
 * @see com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter
 * @since 1.2
 */
public interface SingleValueConverter extends ConverterMatcher {

    /**
     * Marshals an Object into a single value representation.
     * @param obj the Object to be converted
     * @return a String with the single value of the Object or <code>null</code>
     */
    public String toString(Object obj);

    /**
     * Unmarshals an Object from its single value representation.
     * @param str the String with the single value of the Object
     * @return the Object
     */
    public Object fromString(String str);

}
