/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 17. April 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.xml;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;


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
 */
public class XmlFriendlyReplacer {

    private String dollarReplacement;
    private String underscoreReplacement;
    private transient Map escapeCache;
    private transient Map unescapeCache;

    /**
     * Default constructor.
     */
    public XmlFriendlyReplacer() {
        this("_-", "__");
    }

    /**
     * Creates an XmlFriendlyReplacer with custom replacements
     * 
     * @param dollarReplacement the replacement for '$'
     * @param underscoreReplacement the replacement for '_'
     */
    public XmlFriendlyReplacer(String dollarReplacement, String underscoreReplacement) {
        this.dollarReplacement = dollarReplacement;
        this.underscoreReplacement = underscoreReplacement;
        escapeCache = new WeakHashMap();
        unescapeCache = new WeakHashMap();
    }

    /**
     * Escapes name substituting '$' and '_' with replacement strings
     * 
     * @param name the name of attribute or node
     * @return The String with the escaped name
     */
    public String escapeName(String name) {
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
                    result.append(underscoreReplacement);
                } else {
                    result.append(c);
                }
            }
            s = result.toString();
            escapeCache.put(name, new WeakReference(s));
        }
        return s;
    }

    /**
     * Unescapes name re-enstating '$' and '_' when replacement strings are found
     * 
     * @param name the name of attribute or node
     * @return The String with unescaped name
     */
    public String unescapeName(String name) {
        final WeakReference ref = (WeakReference)unescapeCache.get(name);
        String s = (String)(ref == null ? null : ref.get());

        if (s == null) {
            final char dollarReplacementFirstChar = dollarReplacement.charAt(0);
            final char underscoreReplacementFirstChar = underscoreReplacement.charAt(0);
            final int length = name.length();

            // First, fast (common) case: nothing to unescape
            int i = 0;

            for (; i < length; i++ ) {
                char c = name.charAt(i);
                // We'll do a quick check for potential match
                if (c == dollarReplacementFirstChar || c == underscoreReplacementFirstChar) {
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
                } else if (c == underscoreReplacementFirstChar
                    && name.startsWith(underscoreReplacement, i)) {
                    i += underscoreReplacement.length() - 1;
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

    private Object readResolve() {
        escapeCache = new WeakHashMap();
        unescapeCache = new WeakHashMap();
        return this;
    }
}
