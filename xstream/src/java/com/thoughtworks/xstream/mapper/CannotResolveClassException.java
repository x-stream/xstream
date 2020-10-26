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

package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.XStreamException;


/**
 * Exception thrown if a mapper cannot locate the appropriate class for an element.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class CannotResolveClassException extends XStreamException {
    private static final long serialVersionUID = 10400L;

    public CannotResolveClassException(final String className) {
        super(className);
    }

    /**
     * @since 1.4.2
     */
    public CannotResolveClassException(final String className, final Throwable cause) {
        super(className, cause);
    }
}
