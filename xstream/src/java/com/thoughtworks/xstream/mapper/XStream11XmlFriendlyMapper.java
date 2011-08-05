/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. May 2006 by Mauro Talevi
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
 * 
 * Note, this class is no longer in regular use for current XStream versions. It exists to provide backward compatibility
 * to existing XML data written with older XStream versions (&lt;= 1.1).
 * 
 * @author Joe Walnes
 * @author Mauro Talevi
 * @deprecated As of 1.4 use {@link com.thoughtworks.xstream.io.xml.XmlFriendlyReader}
 */
public class XStream11XmlFriendlyMapper extends AbstractXmlFriendlyMapper {

    public XStream11XmlFriendlyMapper(Mapper wrapped) {
        super(wrapped);
    }

    public Class realClass(String elementName) {
        return super.realClass(unescapeClassName(elementName));
    }

    public String realMember(Class type, String serialized) {
        return unescapeFieldName(super.realMember(type, serialized));
    }

    public String mapNameFromXML(String xmlName) {
        return unescapeFieldName(xmlName);
    }
}
