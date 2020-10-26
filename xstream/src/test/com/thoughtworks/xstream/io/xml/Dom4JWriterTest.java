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

package com.thoughtworks.xstream.io.xml;

import org.dom4j.Branch;


public class Dom4JWriterTest extends AbstractDocumentWriterTest<Branch> {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        writer = new Dom4JWriter();
    }

    @Override
    protected DocumentReader createDocumentReaderFor(final Branch node) {
        return new Dom4JReader(node);
    }

    // inherits tests from superclass
}
