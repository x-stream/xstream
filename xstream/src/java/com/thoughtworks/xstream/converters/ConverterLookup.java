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
 * Responsible for looking up the correct Converter implementation for a specific type.
 * 
 * @author Joe Walnes
 * @see Converter
 */
public interface ConverterLookup {

    /**
     * Lookup a converter for a specific type.
     * <p>
     * This type may be any Class, including primitive and array types. It may also be null, signifying the value to be
     * converted is a null type.
     * </p>
     */
    Converter lookupConverterForType(Class<?> type);
}
