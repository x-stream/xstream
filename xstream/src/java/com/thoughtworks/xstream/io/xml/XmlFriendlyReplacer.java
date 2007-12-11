/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
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
 * Allows replacement of Strings in XML-friendly drivers.
 * 
 * The default replacements are:
 * <ul>
 * <li><b>$</b> (dollar) chars are replaced with <b>_-</b> (underscore dash) string.<br></li>
 * <li><b>_</b> (underscore) chars are replaced with <b>__</b> (double underscore) string.<br></li>
 * </ul>
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class XmlFriendlyReplacer {

    private String dollarReplacement;
    private String underscoreReplacement;

    /**
     * Default constructor. 
     */
    public XmlFriendlyReplacer() {
        this("_-", "__");
    }
    
    /**
     * Creates an XmlFriendlyReplacer with custom replacements
     * @param dollarReplacement the replacement for '$'
     * @param underscoreReplacement the replacement for '_'
     */
    public XmlFriendlyReplacer(String dollarReplacement, String underscoreReplacement) {
        this.dollarReplacement = dollarReplacement;
        this.underscoreReplacement = underscoreReplacement;
    }
    
    /**
     * Escapes name substituting '$' and '_' with replacement strings
     * @param name the name of attribute or node
     * @return The String with the escaped name
     */
    public String escapeName(String name) {
        StringBuffer result = new StringBuffer();
        int length = name.length();
        for(int i = 0; i < length; i++) {
            char c = name.charAt(i);
            if (c == '$' ) {
                result.append(dollarReplacement);
            } else if (c == '_') {
                result.append(underscoreReplacement);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    /**
     * Unescapes name re-enstating '$' and '_' when replacement strings are found
     * @param name the name of attribute or node
     * @return The String with unescaped name
     */
    public String unescapeName(String name) {
        final int underscoreReplacementInc = underscoreReplacement.length() - 1;
        final int dollarReplacementInc = dollarReplacement.length() - 1;
        final int length = name.length();
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < length; i++) {
            final char c = name.charAt(i);
            if (name.startsWith(dollarReplacement, i)) {
                i += dollarReplacementInc;
                result.append('$');
            } else if (name.startsWith(underscoreReplacement, i)) {
                i += underscoreReplacementInc;
                result.append('_');
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
