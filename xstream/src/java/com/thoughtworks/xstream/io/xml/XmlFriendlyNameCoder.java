/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 15. August 2009 by Joerg Schaible, copied from XmlFriendlyReplacer.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.io.naming.NameCoder;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Encode and decode tag and attribute names in XML drivers.
 * <p>
 * This NameCoder is designed to ensure the correct encoding and decoding of names used for Java
 * types and fields to XML tags and attribute names.
 * </p>
 * <p>
 * The default replacements are:
 * </p>
 * <ul>
 * <li><b>$</b> (dollar) chars are replaced with <b>_-</b> (underscore dash) string.<br>
 * </li>
 * <li><b>_</b> (underscore) chars are replaced with <b>__</b> (double underscore) string.<br>
 * </li>
 * </ul>
 * 
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @author Tatu Saloranta
 * @since upcoming
 */
public class XmlFriendlyNameCoder implements NameCoder, Cloneable {

    private final String dollarReplacement;
    private final String escapeCharReplacement;
    private transient Map escapeCache;
    private transient Map unescapeCache;

    /**
     * Construct a new XmlFriendlyNameCoder.
     * 
     * @since upcoming
     */
    public XmlFriendlyNameCoder() {
        this("_-", "__");
    }

    /**
     * Construct a new XmlFriendlyNameCoder with custom replacement strings for dollar and the
     * escape character.
     * 
     * @param dollarReplacement
     * @param escapeCharReplacement
     * @since upcoming
     */
    public XmlFriendlyNameCoder(String dollarReplacement, String escapeCharReplacement) {
        this.dollarReplacement = dollarReplacement;
        this.escapeCharReplacement = escapeCharReplacement;
        readResolve();
    }

    /**
     * {@inheritDoc}
     */
    public String decodeAttribute(String attributeName) {
        return decodeName(attributeName);
    }

    /**
     * {@inheritDoc}
     */
    public String decodeNode(String elementName) {
        return decodeName(elementName);
    }

    /**
     * {@inheritDoc}
     */
    public String encodeAttribute(String name) {
        return encodeName(name);
    }

    /**
     * {@inheritDoc}
     */
    public String encodeNode(String name) {
        return encodeName(name);
    }

    private String encodeName(String name) {
        final WeakReference ref = (WeakReference)escapeCache.get(name);
        String s = (String)(ref == null ? null : ref.get());

        if (s == null) {
            final int length = name.length();

            // First, fast (common) case: nothing to escape
            int i = 0;

            for (; i < length; i++ ) {
                char c = name.charAt(i);
                if (c == '$' || c == '_') {
                    break;
                }
            }

            if (i == length) {
                return name;
            }

            // Otherwise full processing
            final StringBuffer result = new StringBuffer(length + 8);

            // We know first N chars are safe
            if (i > 0) {
                result.append(name.substring(0, i));
            }

            for (; i < length; i++ ) {
                char c = name.charAt(i);
                if (c == '$') {
                    result.append(dollarReplacement);
                } else if (c == '_') {
                    result.append(escapeCharReplacement);
                } else {
                    result.append(c);
                }
            }
            s = result.toString();
            escapeCache.put(name, new WeakReference(s));
        }
        return s;
    }

    private String decodeName(String name) {
        final WeakReference ref = (WeakReference)unescapeCache.get(name);
        String s = (String)(ref == null ? null : ref.get());

        if (s == null) {
            final char dollarReplacementFirstChar = dollarReplacement.charAt(0);
            final char escapeReplacementFirstChar = escapeCharReplacement.charAt(0);
            final int length = name.length();

            // First, fast (common) case: nothing to decode
            int i = 0;

            for (; i < length; i++ ) {
                char c = name.charAt(i);
                // We'll do a quick check for potential match
                if (c == dollarReplacementFirstChar || c == escapeReplacementFirstChar) {
                    // and if it might be a match, just quit, will check later on
                    break;
                }
            }

            if (i == length) {
                return name;
            }

            // Otherwise full processing
            final StringBuffer result = new StringBuffer(length + 8);

            // We know first N chars are safe
            if (i > 0) {
                result.append(name.substring(0, i));
            }

            for (; i < length; i++ ) {
                char c = name.charAt(i);
                if (c == dollarReplacementFirstChar && name.startsWith(dollarReplacement, i)) {
                    i += dollarReplacement.length() - 1;
                    result.append('$');
                } else if (c == escapeReplacementFirstChar
                    && name.startsWith(escapeCharReplacement, i)) {
                    i += escapeCharReplacement.length() - 1;
                    result.append('_');
                } else {
                    result.append(c);
                }
            }

            s = result.toString();
            unescapeCache.put(name, new WeakReference(s));
        }
        return s;
    }

    public Object clone() {
        try {
            XmlFriendlyNameCoder coder = (XmlFriendlyNameCoder)super.clone();
            coder.readResolve();
            return coder;

        } catch (CloneNotSupportedException e) {
            throw new ObjectAccessException("Cannot clone myself", e);
        }
    }

    private Object readResolve() {
        escapeCache = new WeakHashMap();
        unescapeCache = new WeakHashMap();
        return this;
    }
}
