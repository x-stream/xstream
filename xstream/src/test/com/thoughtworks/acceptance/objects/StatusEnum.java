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

package com.thoughtworks.acceptance.objects;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class StatusEnum implements Serializable, Comparable<StatusEnum> {
    private static final long serialVersionUID = 200405L;
    private static int nextOrdinal = 0;
    private final int ordinal = nextOrdinal++;

    public static final StatusEnum STARTED = new StatusEnum("STARTED");

    public static final StatusEnum FINISHED = new StatusEnum("FINISHED");

    private static final StatusEnum[] PRIVATE_VALUES = {STARTED, FINISHED};

    public static final List<StatusEnum> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

    private String name; // for debug only

    private StatusEnum(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(final StatusEnum o) {
        return ordinal - o.ordinal;
    }

    private Object readResolve() {
        return PRIVATE_VALUES[ordinal]; // Canonicalize
    }
}
