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

package com.thoughtworks.xstream.tools.benchmark.targets;

import com.thoughtworks.xstream.tools.benchmark.Target;

/**
 * A small java.lang.String target.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
public class StringTarget implements Target {

    private final String string = "Hello World!";

    public String toString() {
        return "Simple string";
    }

    public Object target() {
        return string;
    }

    public boolean isEqual(Object other) {
        return string.equals(other);
    }

}
