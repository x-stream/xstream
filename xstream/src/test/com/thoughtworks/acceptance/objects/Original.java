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

/**
 * @author Joe Walnes
 */
package com.thoughtworks.acceptance.objects;

public class Original extends StandardObject {
    private static final long serialVersionUID = 200810L;
    String originalValue;

    public Original() {
    }

    public Original(final String originalValue) {
        this.originalValue = originalValue;
    }

    private Object writeReplace() {
        return new Replaced(originalValue.toUpperCase());
    }
}
