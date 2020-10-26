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
 * Thrown by {@link Converter} implementations when they cannot convert an object to/from textual data. When this
 * exception is thrown it can be passed around to things that accept an {@link ErrorWriter}, allowing them to add
 * diagnostics to the stack trace.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @see ErrorWriter
 */
public class ConversionException extends ErrorWritingException {
    private static final long serialVersionUID = 20160226L;

    public ConversionException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    public ConversionException(final String msg) {
        super(msg);
    }

    public ConversionException(final Throwable cause) {
        super(cause);
    }
}
