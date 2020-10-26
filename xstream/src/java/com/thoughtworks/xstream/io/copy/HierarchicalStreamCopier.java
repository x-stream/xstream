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

package com.thoughtworks.xstream.io.copy;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Tool for copying the contents of one HierarichalStreamReader to a HierarichalStreamWriter.
 * <p>
 * This is useful for transforming the output of one format to another (e.g. binary to XML)
 * without needing to know details about the classes and avoiding the overhead of serialization.</p>
 *
 * <h3>Example</h3>
 * <pre>
 * HierarchicalStreamReader reader = new BinaryStreamReader(someBinaryInput);
 * HierarchicalStreamWriter writer = new PrettyPrintWriter(someXmlOutput);
 * HierarchicalStreamCopier copier = new HierarchicalStreamCopier();
 * copier.copy(reader, writer);
 * </pre>
 *
 * @author Joe Walnes
 * @since 1.2
 */
public class HierarchicalStreamCopier {
    public void copy(HierarchicalStreamReader source, HierarchicalStreamWriter destination) {
        destination.startNode(source.getNodeName());
        int attributeCount = source.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            destination.addAttribute(source.getAttributeName(i), source.getAttribute(i));
        }
        String value = source.getValue();
        if (value != null && value.length() > 0) {
            destination.setValue(value);
        }
        while (source.hasMoreChildren()) {
            source.moveDown();
            copy(source, destination);
            source.moveUp();
        }
        destination.endNode();
    }
}
