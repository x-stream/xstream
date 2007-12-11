/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. April 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.xml;

/**
 * Allows replacement of Strings in xml-friendly drivers to provide compatibility with XStream 1.1 format
 *  
 * @author Mauro Talevi
 * @since 1.2
 */
public class XStream11XmlFriendlyReplacer extends XmlFriendlyReplacer {

    private char dollarReplacementInClass = '-';
    private String dollarReplacementInField = "_DOLLAR_";
    private String underscoreReplacementInField = "__";
    private String noPackagePrefix = "default";
    
    /**
     * Default constructor. 
     */
    public XStream11XmlFriendlyReplacer() {
    }

    /**
     * Noop implementation that does not unescape name.  Used for XStream 1.1 compatibility.
     * @param name the name of attribute or node
     * @return The String with unescaped name
     */
    public String unescapeName(String name) {
        return name;
    }
        
    protected String escapeClassName(String className) {
        // the $ used in inner class names is illegal as an xml element getNodeName
        className = className.replace('$', dollarReplacementInClass);

        // special case for classes named $Blah with no package; <-Blah> is illegal XML
        if (className.charAt(0) == dollarReplacementInClass) {
            className = noPackagePrefix + className;
        }

        return className;
    }
    
    protected String escapeFieldName(String fieldName) {
        StringBuffer result = new StringBuffer();
        int length = fieldName.length();
        for(int i = 0; i < length; i++) {
            char c = fieldName.charAt(i);
            if (c == '$' ) {
                result.append(dollarReplacementInField);
            } else if (c == '_') {
                result.append(underscoreReplacementInField);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }    
    
    protected String unescapeClassName(String className) {
        // special case for classes named $Blah with no package; <-Blah> is illegal XML
        if (className.startsWith(noPackagePrefix+dollarReplacementInClass)) {
            className = className.substring(noPackagePrefix.length());
        }

        // the $ used in inner class names is illegal as an xml element getNodeName
        className = className.replace(dollarReplacementInClass, '$');

        return className;
    }

    protected String unescapeFieldName(String xmlName) {
        StringBuffer result = new StringBuffer();
        int length = xmlName.length();
        for(int i = 0; i < length; i++) {
            char c = xmlName.charAt(i);
            if ( stringFoundAt(xmlName, i,underscoreReplacementInField)) {
                i +=underscoreReplacementInField.length() - 1;
                result.append('_');
            } else if ( stringFoundAt(xmlName, i,dollarReplacementInField)) {
                i +=dollarReplacementInField.length() - 1;
                result.append('$');
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private boolean stringFoundAt(String name, int i, String replacement) {
        if ( name.length() >= i + replacement.length() 
          && name.substring(i, i + replacement.length()).equals(replacement) ){
            return true;
        }
        return false;
    }
    
}
