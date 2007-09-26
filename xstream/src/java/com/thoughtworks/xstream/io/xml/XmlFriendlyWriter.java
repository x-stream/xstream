/*
 * Copyright (C) 2007 XStream Committers.
 * Created on 26.09.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

/**
 * An interface for a {@link com.thoughtworks.xstream.io.HierarchicalStreamWriter} supporting XML-friendly names.
 * 
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @since upcoming
 */
public interface XmlFriendlyWriter {

    /**
     * Escapes XML name (node or attribute) to be XML-friendly
     * 
     * @param name the unescaped XML name
     * @return An escaped name with original characters replaced
     */
    String escapeXmlName(String name);
}
