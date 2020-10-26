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

package com.thoughtworks.xstream.testutil;

import junit.framework.Assert;

public class CallLog {

    private StringBuffer expected = new StringBuffer();
    private StringBuffer actual = new StringBuffer();

    public void expect(String message) {
        expected.append(message).append('\n');
    }

    public void actual(String message) {
        actual.append(message).append('\n');
    }

    public void verify() {
        Assert.assertEquals(expected.toString(), actual.toString());
        reset();
    }

    public void reset() {
        expected = new StringBuffer();
        actual = new StringBuffer();
    }

}
