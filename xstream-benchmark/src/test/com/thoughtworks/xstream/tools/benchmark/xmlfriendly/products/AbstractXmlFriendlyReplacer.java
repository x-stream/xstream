/*
 * Copyright (C) 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. September 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.xmlfriendly.products;

import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Abstract base class for the XmlFriendlyReplacer with all kind of implementations.
 * 
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @author Tatu Saloranta
 */
public abstract class AbstractXmlFriendlyReplacer extends XmlFriendlyReplacer {

    private final String dollarReplacement;
    private final String underscoreReplacement;
    private final int bufferIncrement;
    private final Map escapeCache;
    private final Map unescapeCache;

    /**
     * Creates an XmlFriendlyReplacer with custom replacements
     * 
     * @param dollarReplacement the replacement for '$'
     * @param underscoreReplacement the replacement for '_'
     * @param bufferIncrement buffer increment for preallocation
     */
    public AbstractXmlFriendlyReplacer(
        final String dollarReplacement, final String underscoreReplacement,
        final int bufferIncrement) {
        this.dollarReplacement = dollarReplacement;
        this.underscoreReplacement = underscoreReplacement;
        this.bufferIncrement = bufferIncrement;
        escapeCache = new WeakHashMap();
        unescapeCache = new WeakHashMap();
    }

    /**
     * Escapes name substituting '$' and '_' with replacement strings
     * 
     * @param name the name of attribute or node
     * @return The String with the escaped name
     */
    public abstract String escapeName(String name);

    /**
     * Escapes name substituting '$' and '_' with replacement strings
     * 
     * @param name the name of attribute or node
     * @return The String with the escaped name
     */
    public abstract String unescapeName(String name);

    protected String escapeNoName(final String name) {
        return name;
    }

    protected String unescapeNoName(final String name) {
        return name;
    }

    protected String escapeIterativelyAppending(final String name) {
        final int length = name.length();
        final StringBuffer result = bufferIncrement == 0
            ? new StringBuffer()
            : new StringBuffer(length + bufferIncrement);
        for (int i = 0; i < length; i++) {
            final char c = name.charAt(i);
            if (c == '$') {
                result.append(dollarReplacement);
            } else if (c == '_') {
                result.append(underscoreReplacement);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    protected String unescapeIterativelyAppending(final String name) {
        final int underscoreReplacementInc = underscoreReplacement.length() - 1;
        final int dollarReplacementInc = dollarReplacement.length() - 1;
        final int length = name.length();
        final StringBuffer result = bufferIncrement == 0
            ? new StringBuffer()
            : new StringBuffer(length + bufferIncrement);
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

    protected String escapeByCombinedLookupAppending(final String name) {
        final int length = name.length();
        final StringBuffer result = bufferIncrement == 0
            ? new StringBuffer()
            : new StringBuffer(length + bufferIncrement);
        int posDollar = 0;
        int posUnderscore = 0;
        for (int i = 0; i < length;) {
            if (posUnderscore >= 0) {
                posUnderscore = name.indexOf('_', i);
            }
            if (posDollar >= 0) {
                posDollar = name.indexOf('$', i);
            }
            if (posDollar == -1 && posUnderscore == -1) {
                if (i < length) {
                    result.append(name.substring(i));
                }
                break;
            } else if (posDollar >= 0 && (posUnderscore == -1 || posUnderscore >= posDollar)) {
                result.append(name.substring(i, posDollar));
                result.append(dollarReplacement);
                i = posDollar + 1;
            } else if (posUnderscore >= 0) {
                result.append(name.substring(i, posUnderscore));
                result.append(underscoreReplacement);
                i = posUnderscore + 1;
            }
        }
        return result.toString();
    }

    protected String unescapeByCombinedLookupAppending(final String name) {
        final int underscoreReplacementLength = underscoreReplacement.length();
        final int dollarReplacementLength = dollarReplacement.length();
        final int length = name.length();
        final StringBuffer result = bufferIncrement == 0
            ? new StringBuffer()
            : new StringBuffer(length + bufferIncrement);
        int posDollar = 0;
        int posUnderscore = 0;
        for (int i = 0; i < length;) {
            if (posUnderscore >= 0) {
                posUnderscore = name.indexOf(underscoreReplacement, i);
            }
            if (posDollar >= 0) {
                posDollar = name.indexOf(dollarReplacement, i);
            }
            if (posDollar == -1 && posUnderscore == -1) {
                if (i < length) {
                    result.append(name.substring(i));
                }
                break;
            } else if (posDollar >= 0 && (posUnderscore == -1 || posUnderscore >= posDollar)) {
                result.append(name.substring(i, posDollar));
                result.append('$');
                i = posDollar + dollarReplacementLength;
            } else if (posUnderscore >= 0) {
                result.append(name.substring(i, posUnderscore));
                result.append('_');
                i = posUnderscore + underscoreReplacementLength;
            }
        }
        return result.toString();
    }

    protected String escapeByCombinedLookupReplacing(final String name) {
        final int underscoreReplacementLength = underscoreReplacement.length();
        final int dollarReplacementLength = dollarReplacement.length();
        final StringBuffer result;
        if (bufferIncrement == 0) {
            result = new StringBuffer(name);
        } else {
            result = new StringBuffer(name.length() + bufferIncrement);
            result.append(name);
        }
        int posDollar = 0;
        int posUnderscore = 0;
        int i = 0;
        while (true) {
            if (posUnderscore >= 0) {
                posUnderscore = result.indexOf("_", i);
            }
            if (posDollar >= 0) {
                posDollar = result.indexOf("$", i);
            }
            if (posDollar == -1 && posUnderscore == -1) {
                break;
            } else if (posDollar >= 0 && (posUnderscore == -1 || posUnderscore > posDollar)) {
                result.replace(posDollar, posDollar + 1, dollarReplacement);
                i = posDollar + dollarReplacementLength;
            } else if (posUnderscore >= 0) {
                result.replace(posUnderscore, posUnderscore + 1, underscoreReplacement);
                i = posUnderscore + underscoreReplacementLength;
            }
        }
        return result.toString();
    }

    protected String unescapeByCombinedLookupReplacing(final String name) {
        final int underscoreReplacementLength = underscoreReplacement.length();
        final int dollarReplacementLength = dollarReplacement.length();
        final StringBuffer result;
        if (bufferIncrement == 0) {
            result = new StringBuffer(name);
        } else {
            result = new StringBuffer(name.length() + bufferIncrement);
            result.append(name);
        }
        int posDollar = 0;
        int posUnderscore = 0;
        int i = 0;
        while (true) {
            if (posUnderscore >= 0) {
                posUnderscore = result.indexOf(underscoreReplacement, i);
            }
            if (posDollar >= 0) {
                posDollar = result.indexOf(dollarReplacement, i);
            }
            if (posDollar == -1 && posUnderscore == -1) {
                break;
            } else if (posDollar >= 0 && (posUnderscore == -1 || posUnderscore >= posDollar)) {
                result.replace(posDollar, posDollar + dollarReplacementLength, "$");
                i = posDollar + 1;
            } else if (posUnderscore >= 0) {
                result.replace(posUnderscore, posUnderscore + underscoreReplacementLength, "_");
                i = posUnderscore + 1;
            }
        }
        return result.toString();
    }

    protected String escapeBySeparateLookupReplacing(final String name) {
        final StringBuffer result;
        if (bufferIncrement == 0) {
            result = new StringBuffer(name);
        } else {
            result = new StringBuffer(name.length() + bufferIncrement);
            result.append(name);
        }
        final int underscoreReplacementInc = underscoreReplacement.length();
        final int dollarReplacementInc = dollarReplacement.length();
        int inc = 0;
        int pos = 0;

        while ((pos = result.indexOf("_", pos + inc)) != -1) {
            result.replace(pos, pos + 1, underscoreReplacement);
            inc = underscoreReplacementInc;
        }

        inc = 0;
        pos = 0;
        while ((pos = result.indexOf("$", pos + inc)) != -1) {
            result.replace(pos, pos + 1, dollarReplacement);
            inc = dollarReplacementInc;
        }
        return result.toString();
    }

    protected String unescapeBySeparateLookupReplacing(final String name) {
        final StringBuffer result;
        if (bufferIncrement == 0) {
            result = new StringBuffer(name);
        } else {
            result = new StringBuffer(name.length() + bufferIncrement);
            result.append(name);
        }
        int dollarReplacementLength = dollarReplacement.length();
        int pos = -dollarReplacementLength;
        while ((pos = result.indexOf(dollarReplacement, pos + 1)) != -1) {
            result.replace(pos, pos + dollarReplacementLength, "$");
        }

        int underscoreReplacementLength = underscoreReplacement.length();
        pos = -underscoreReplacementLength;

        while ((pos = result.indexOf(underscoreReplacement, pos + 1)) != -1) {
            result.replace(pos, pos + underscoreReplacementLength, "_");
        }

        return result.toString();
    }

    protected String escapeIterativelyReplacing(final String name) {
        int length = name.length();
        final int underscoreReplacementInc = underscoreReplacement.length() - 1;
        final int dollarReplacementInc = dollarReplacement.length() - 1;
        final StringBuffer result;
        if (bufferIncrement == 0) {
            result = new StringBuffer(name);
        } else {
            result = new StringBuffer(length + bufferIncrement);
            result.append(name);
        }
        for (int i = 0; i < length; i++) {
            final char c = result.charAt(i);
            if (c == '$') {
                result.replace(i, i + 1, dollarReplacement);
                length += dollarReplacementInc;
                i += dollarReplacementInc;
            } else if (c == '_') {
                result.replace(i, i + 1, underscoreReplacement);
                length += underscoreReplacementInc;
                i += underscoreReplacementInc;
            }
        }
        return result.toString();
    }

    protected String unescapeIterativelyReplacing(final String name) {
        final char dollarChar = dollarReplacement.charAt(0);
        final char underscoreChar = underscoreReplacement.charAt(0);
        final int underscoreReplacementLength = underscoreReplacement.length();
        final int dollarReplacementLength = dollarReplacement.length();
        int length = name.length();
        final StringBuffer result;
        if (bufferIncrement == 0) {
            result = new StringBuffer(name);
        } else {
            result = new StringBuffer(length + bufferIncrement);
            result.append(name);
        }
        for (int i = 0; i < length; ++i) {
            final char c = result.charAt(i);
            if (c == dollarChar
                && i + dollarReplacementLength <= length
                && result.substring(i, i + dollarReplacementLength).equals(dollarReplacement)) {
                result.replace(i, i + dollarReplacementLength, "$");
                length -= dollarReplacementLength - 1;
            } else if (c == underscoreChar
                && i + underscoreReplacementLength <= length
                && result.substring(i, i + underscoreReplacementLength).equals(
                    underscoreReplacement)) {
                result.replace(i, i + underscoreReplacementLength, "_");
                length -= underscoreReplacementLength - 1;
            }
        }
        return result.toString();
    }

    protected String escapeIterativelyAppendingWithShortcut(String name) {
        final int length = name.length();

        // First, fast (common) case: nothing to escape
        int i = 0;

        for (; i < length; i++) {
            char c = name.charAt(i);
            if (c == '$' || c == '_') {
                break;
            }
        }

        if (i == length) {
            return name;
        }

        // Otherwise full processing
        final StringBuffer result = new StringBuffer(length+8);

        // We know first N chars are safe
        if (i > 0) {
            result.append(name.substring(0, i));
        }

        for (; i < length; i++) {
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

    protected String unescapeIterativelyAppendingWithShortcut(String name) {
        final char dollarReplacementFirstChar = dollarReplacement.charAt(0);
        final char underscoreReplacementFirstChar = underscoreReplacement.charAt(0);
        final int length = name.length();

        // First, fast (common) case: nothing to unescape
        int i = 0;

        for (; i < length; i++) {
            char c = name.charAt(i);
            // We'll do a quick check for potential match
            if (c == dollarReplacementFirstChar
                || c == underscoreReplacementFirstChar) {
                // and if it might be a match, just quit, will check later on
                break;
            }
        }

        if (i == length) {
            return name;
        }

        // Otherwise full processing
        final StringBuffer result = new StringBuffer(length+8);

        // We know first N chars are safe
        if (i > 0) {
            result.append(name.substring(0, i));
        }

        for (; i < length; i++) {
            char c = name.charAt(i);
            if (c == dollarReplacementFirstChar
                && name.startsWith(dollarReplacement, i)) {
                i += dollarReplacement.length()-1;
                result.append('$');
            } else if (c == underscoreReplacementFirstChar
                       && name.startsWith(underscoreReplacement, i)) {
                i += underscoreReplacement.length()-1;
                result.append('_');
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
    
    protected String escapeCachingIterativelyAppendingWithShortcut(String name) {
        final WeakReference ref = (WeakReference)escapeCache.get(name);
        String s = (String)(ref == null ? null : ref.get());

        if (s == null) {
            s = escapeIterativelyAppendingWithShortcut(name);
            escapeCache.put(name, new WeakReference(s));
        }
        return s;
    }
    
    protected String unescapeCachingIterativelyAppendingWithShortcut(String name) {
        final WeakReference ref = (WeakReference)unescapeCache.get(name);
        String s = (String)(ref == null ? null : ref.get());
    
        if (s == null) {
            s = unescapeIterativelyAppendingWithShortcut(name);
            unescapeCache.put(name, new WeakReference(s));
        }
        return s;
    }
}
