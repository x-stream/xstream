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

package com.thoughtworks.xstream.io.xml.xppdom;

/**
 * Simple Document Object Model for XmlPullParser implementations.
 * 
 * @author Jason van Zyl
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @deprecated As of 1.4, use {@link XppDom} instead
 */
@Deprecated
public class Xpp3Dom extends XppDom {
    private static final long serialVersionUID = 10400L;

    /**
     * @deprecated As of 1.4, use {@link XppDom} instead
     */
    @Deprecated
    public Xpp3Dom(final String name) {
        super(name);
    }
}
