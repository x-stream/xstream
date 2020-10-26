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

/**
 * An interface for a {@link com.thoughtworks.xstream.io.HierarchicalStreamReader} supporting XML-friendly names.
 * 
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @since 1.3
 * @deprecated As of 1.4
 */
@Deprecated
public interface XmlFriendlyReader {

    /**
     * Unescapes XML-friendly name (node or attribute)
     * 
     * @param name the escaped XML-friendly name
     * @return An unescaped name with original characters
     * @deprecated As of 1.4
     */
    @Deprecated
    String unescapeXmlName(String name);

}
