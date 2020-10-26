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

import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.binary.BinaryStreamWriter;
import com.thoughtworks.xstream.io.binary.BinaryStreamReader;

import java.io.OutputStream;
import java.io.InputStream;

/**
 * Uses XStream with binary format instead of XML.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Product
 * @see XStream
 * @see BinaryStreamReader
 * @see BinaryStreamWriter
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
public class XStreamBinary implements Product {

    private final XStream xstream;

    public XStreamBinary() {
        this.xstream = new XStream();
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.marshal(object, new BinaryStreamWriter(output));
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.unmarshal(new BinaryStreamReader(input));
    }

    public String toString() {
        return "XStream (binary format)";
    }

}
