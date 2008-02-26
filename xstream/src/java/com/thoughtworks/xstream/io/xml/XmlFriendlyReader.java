/*
 * Copyright (C) 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

/**
 * An interface for a {@link com.thoughtworks.xstream.io.HierarchicalStreamReader} supporting XML-friendly names.
 * 
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @since 1.3
 */
public interface XmlFriendlyReader {

    /**
     * Unescapes XML-friendly name (node or attribute) 
     * 
     * @param name the escaped XML-friendly name
     * @return An unescaped name with original characters
     */
    String unescapeXmlName(String name);

}
