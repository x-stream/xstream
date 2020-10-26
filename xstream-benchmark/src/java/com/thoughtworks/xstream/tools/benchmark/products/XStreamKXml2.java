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

package com.thoughtworks.xstream.tools.benchmark.products;

import com.thoughtworks.xstream.io.xml.KXml2Driver;

/**
 * Uses XStream with the kXML2 driver for parsing XML.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see com.thoughtworks.xstream.tools.benchmark.Product
 * @see com.thoughtworks.xstream.XStream
 * @see KXml2Driver
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
public class XStreamKXml2 extends XStreamDriver {

    public XStreamKXml2() {
        super(new KXml2Driver(), "XML with kXML2 parser");
    }
}
