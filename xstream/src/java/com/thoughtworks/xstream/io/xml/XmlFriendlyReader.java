/*
 * Copyright (C) 2007 XStream Committers.
 * Created on 26.09.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

/**
 * An interface for a {@link com.thoughtworks.xstream.io.HierarchicalStreamReader} supporting XML-friendly names.
 * 
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @since upcoming
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
