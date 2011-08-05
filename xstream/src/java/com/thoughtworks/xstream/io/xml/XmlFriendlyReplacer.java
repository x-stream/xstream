/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 17. April 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.xml;

/**
 * Allows replacement of Strings in XML-friendly drivers. The default replacements are:
 * <ul>
 * <li><b>$</b> (dollar) chars are replaced with <b>_-</b> (underscore dash) string.<br>
 * </li>
 * <li><b>_</b> (underscore) chars are replaced with <b>__</b> (double underscore) string.<br>
 * </li>
 * </ul>
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @author Tatu Saloranta
 * @since 1.2
 * @deprecated As of 1.4, use {@link XmlFriendlyNameCoder} instead
 */
public class XmlFriendlyReplacer extends XmlFriendlyNameCoder {

    /**
     * Default constructor.
     * 
     * @deprecated As of 1.4, use {@link XmlFriendlyNameCoder} instead
     */
    public XmlFriendlyReplacer() {
        this("_-", "__");
    }

    /**
     * Creates an XmlFriendlyReplacer with custom replacements
     * 
     * @param dollarReplacement the replacement for '$'
     * @param underscoreReplacement the replacement for '_'
     * @deprecated As of 1.4, use {@link XmlFriendlyNameCoder} instead
     */
    public XmlFriendlyReplacer(String dollarReplacement, String underscoreReplacement) {
        super(dollarReplacement, underscoreReplacement);
    }

    /**
     * Escapes name substituting '$' and '_' with replacement strings
     * 
     * @param name the name of attribute or node
     * @return The String with the escaped name
     * @deprecated As of 1.4, use {@link XmlFriendlyNameCoder} instead
     */
    public String escapeName(String name) {
        return super.encodeNode(name);
    }

    /**
     * Unescapes name re-enstating '$' and '_' when replacement strings are found
     * 
     * @param name the name of attribute or node
     * @return The String with unescaped name
     * @deprecated As of 1.4, use {@link XmlFriendlyNameCoder} instead
     */
    public String unescapeName(String name) {
        return super.decodeNode(name);
    }

}
