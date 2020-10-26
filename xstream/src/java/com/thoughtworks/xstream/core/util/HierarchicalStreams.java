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

package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Helper methods for {@link HierarchicalStreamReader} and {@link HierarchicalStreamWriter}.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3.1
 */
public class HierarchicalStreams {

    public static Class<?> readClassType(final HierarchicalStreamReader reader, final Mapper mapper) {
        final String classAttribute = readClassAttribute(reader, mapper);
        Class<?> type;
        if (classAttribute == null) {
            type = mapper.realClass(reader.getNodeName());
        } else {
            type = mapper.realClass(classAttribute);
        }
        return type;
    }

    public static String readClassAttribute(final HierarchicalStreamReader reader, final Mapper mapper) {
        String attributeName = mapper.aliasForSystemAttribute("resolves-to");
        String classAttribute = attributeName == null ? null : reader.getAttribute(attributeName);
        if (classAttribute == null) {
            attributeName = mapper.aliasForSystemAttribute("class");
            if (attributeName != null) {
                classAttribute = reader.getAttribute(attributeName);
            }
        }
        return classAttribute;
    }

}
