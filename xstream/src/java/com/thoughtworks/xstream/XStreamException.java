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

package com.thoughtworks.xstream;

/**
 * Base exception for all thrown exceptions with XStream.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class XStreamException extends RuntimeException {
    private static final long serialVersionUID = 10400L;

    /**
     * Default constructor.
     *
     * @since 1.3
     */
    protected XStreamException() {
        this("", null);
    }

    /**
     * Constructs an XStreamException with a message.
     *
     * @param message
     * @since 1.3
     */
    public XStreamException(final String message) {
        this(message, null);
    }

    /**
     * Constructs an XStreamException as wrapper for a different causing {@link Throwable}.
     *
     * @param cause
     * @since 1.3
     */
    public XStreamException(final Throwable cause) {
        this("", cause);
    }

    /**
     * Constructs an XStreamException with a message as wrapper for a different causing {@link Throwable}.
     *
     * @param message
     * @param cause
     * @since 1.3
     */
    public XStreamException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
