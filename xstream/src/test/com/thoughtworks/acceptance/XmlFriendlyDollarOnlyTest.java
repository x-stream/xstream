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

package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.DefaultDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;


public class XmlFriendlyDollarOnlyTest extends XmlFriendlyTest {

    @Override
    protected XStream createXStream() {
        final XStream xstream = new XStream(DefaultDriver.create(new XmlFriendlyNameCoder("_-", "_")));
        setupSecurity(xstream);
        xstream.allowTypesByWildcard(getClass().getSuperclass().getName() + "$*");
        return xstream;
    }

    @Override
    protected <T> T assertBothWays(final Object root, final String xml) {
        return super.assertBothWays(root, xml.replaceAll("__", "_"));
    }
}
