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

package com.thoughtworks.xstream.mapper;

/**
 * Mapper that ensures that all names in the serialization stream are read in an XML friendly way.
 * <ul>
 * <li><b>_</b> (underscore) chars appearing in class names are replaced with <b>$</b> (dollar)</li>
 * <li><b>_DOLLAR_</b> string appearing in field names are replaced with <b>$</b> (dollar)</li>
 * <li><b>__</b> string appearing in field names are replaced with <b>_</b> (underscore)</li>
 * <li><b>default</b> is the prefix for class names with no package.</li>
 * </ul>
 * Note, this class is no longer in regular use for current XStream versions. It exists to provide backward
 * compatibility to existing XML data written with older XStream versions (&lt;= 1.1).
 * 
 * @author Joe Walnes
 * @author Mauro Talevi
 * @deprecated As of 1.4 use {@link com.thoughtworks.xstream.io.xml.XmlFriendlyReader}
 */
@Deprecated
public class XStream11XmlFriendlyMapper extends AbstractXmlFriendlyMapper {

    public XStream11XmlFriendlyMapper(final Mapper wrapped) {
        super(wrapped);
    }

    @Override
    public Class<?> realClass(final String elementName) {
        return super.realClass(unescapeClassName(elementName));
    }

    @Override
    public String realMember(final Class<?> type, final String serialized) {
        return unescapeFieldName(super.realMember(type, serialized));
    }

    public String mapNameFromXML(final String xmlName) {
        return unescapeFieldName(xmlName);
    }
}
