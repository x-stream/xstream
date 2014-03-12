/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2014 XStream Committers.
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
 * Mapper that ensures that all names in the serialization stream are XML friendly. The replacement chars and strings
 * are:
 * <ul>
 * <li><b>$</b> (dollar) chars appearing in class names are replaced with <b>_</b> (underscore) chars.<br>
 * </li>
 * <li><b>$</b> (dollar) chars appearing in field names are replaced with <b>_DOLLAR_</b> string.<br>
 * </li>
 * <li><b>_</b> (underscore) chars appearing in field names are replaced with <b>__</b> (double underscore) string.<br>
 * </li>
 * <li><b>default</b> as the prefix for class names with no package.</li>
 * </ul>
 * Note, this class is no longer in regular use for current XStream versions. It exists to provide backward
 * compatibility to existing XML data written with older XStream versions.
 * 
 * @author Joe Walnes
 * @author Mauro Talevi
 * @deprecated As of 1.4 use {@link com.thoughtworks.xstream.io.xml.XmlFriendlyReader}
 */
@Deprecated
public class AbstractXmlFriendlyMapper extends MapperWrapper {

    private final char dollarReplacementInClass = '-';
    private final String dollarReplacementInField = "_DOLLAR_";
    private final String underscoreReplacementInField = "__";
    private final String noPackagePrefix = "default";

    protected AbstractXmlFriendlyMapper(final Mapper wrapped) {
        super(wrapped);
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

    protected String unescapeClassName(String className) {
        // special case for classes named $Blah with no package; <-Blah> is illegal XML
        if (className.startsWith(noPackagePrefix + dollarReplacementInClass)) {
            className = className.substring(noPackagePrefix.length());
        }

        // the $ used in inner class names is illegal as an xml element getNodeName
        className = className.replace(dollarReplacementInClass, '$');

        return className;
    }

    protected String escapeFieldName(final String fieldName) {
        final StringBuilder result = new StringBuilder();
        final int length = fieldName.length();
        for (int i = 0; i < length; i++) {
            final char c = fieldName.charAt(i);
            if (c == '$') {
                result.append(dollarReplacementInField);
            } else if (c == '_') {
                result.append(underscoreReplacementInField);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    protected String unescapeFieldName(final String xmlName) {
        final StringBuilder result = new StringBuilder();
        final int length = xmlName.length();
        for (int i = 0; i < length; i++) {
            final char c = xmlName.charAt(i);
            if (stringFoundAt(xmlName, i, underscoreReplacementInField)) {
                i += underscoreReplacementInField.length() - 1;
                result.append('_');
            } else if (stringFoundAt(xmlName, i, dollarReplacementInField)) {
                i += dollarReplacementInField.length() - 1;
                result.append('$');
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private boolean stringFoundAt(final String name, final int i, final String replacement) {
        if (name.length() >= i + replacement.length()
            && name.substring(i, i + replacement.length()).equals(replacement)) {
            return true;
        }
        return false;
    }

}
