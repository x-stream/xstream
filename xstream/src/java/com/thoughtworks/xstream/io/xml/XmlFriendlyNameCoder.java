/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011 XStream Committers.
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
import com.thoughtworks.xstream.core.util.WeakCache;
import com.thoughtworks.xstream.io.naming.NameCoder;

import java.util.ArrayList;
import java.util.Map;


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
 * <li><b>$</b> (dollar) chars are replaced with <b>_-</b> (underscore dash) string.</li>
 * <li><b>_</b> (underscore) chars are replaced with <b>__</b> (double underscore) string.</li>
 * <li>other characters that are invalid in XML names are encoded with <b>_.XXXX</b> (underscore
 * dot followed by hex representation of character).</li>
 * </ul>
 * 
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @author Tatu Saloranta
 * @author Michael Schnell
 * @see <a href="http://www.w3.org/TR/REC-xml/#dt-name">XML 1.0 name definition</a>
 * @see <a href="http://www.w3.org/TR/xml11/#dt-name">XML 1.1 name definition</a>
 * @see <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.8">Java
 *      identifier definition</a>
 * @since 1.4
 */
public class XmlFriendlyNameCoder implements NameCoder, Cloneable {
    private static final IntPair[] XML_NAME_START_CHAR_BOUNDS;
    private static final IntPair[] XML_NAME_CHAR_EXTRA_BOUNDS;
    static {
        class IntPairList extends ArrayList {
            void add(int min, int max) {
                super.add(new IntPair(min, max));
            }

            void add(char cp) {
                super.add(new IntPair(cp, cp));
            }
        }

        // legal characters in XML names according to
        // http://www.w3.org/TR/REC-xml/#NT-Name and
        // http://www.w3.org/TR/xml11/#NT-Name
        IntPairList list = new IntPairList();

        list.add(':');
        list.add('A', 'Z');
        list.add('a', 'z');
        list.add('_');

        list.add(0xC0, 0xD6);
        list.add(0xD8, 0xF6);
        list.add(0xF8, 0x2FF);
        list.add(0x370, 0x37D);
        list.add(0x37F, 0x1FFF);
        list.add(0x200C, 0x200D);
        list.add(0x2070, 0x218F);
        list.add(0x2C00, 0x2FEF);
        list.add(0x3001, 0xD7FF);
        list.add(0xF900, 0xFDCF);
        list.add(0xFDF0, 0xFFFD);
        list.add(0x10000, 0xEFFFF);
        XML_NAME_START_CHAR_BOUNDS = (IntPair[])list.toArray(new IntPair[list.size()]);

        list.clear();
        list.add('-');
        list.add('.');
        list.add('0', '9');
        list.add('\u00b7');
        list.add(0x0300, 0x036F);
        list.add(0x203F, 0x2040);
        XML_NAME_CHAR_EXTRA_BOUNDS = (IntPair[])list.toArray(new IntPair[list.size()]);
    }

    private final String dollarReplacement;
    private final String escapeCharReplacement;
    private transient Map escapeCache;
    private transient Map unescapeCache;
    private final String hexPrefix;

    /**
     * Construct a new XmlFriendlyNameCoder.
     * 
     * @since 1.4
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
     * @since 1.4
     */
    public XmlFriendlyNameCoder(String dollarReplacement, String escapeCharReplacement) {
        this(dollarReplacement, escapeCharReplacement, "_.");
    }

    /**
     * Construct a new XmlFriendlyNameCoder with custom replacement strings for dollar, the
     * escape character and the prefix for hexadecimal encoding of invalid characters in XML
     * names.
     * 
     * @param dollarReplacement
     * @param escapeCharReplacement
     * @since 1.4
     */
    public XmlFriendlyNameCoder(
        String dollarReplacement, String escapeCharReplacement, String hexPrefix) {
        this.dollarReplacement = dollarReplacement;
        this.escapeCharReplacement = escapeCharReplacement;
        this.hexPrefix = hexPrefix;
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
        String s = (String)escapeCache.get(name);
        if (s == null) {
            final int length = name.length();

            // First, fast (common) case: nothing to escape
            int i = 0;

            for (; i < length; i++ ) {
                char c = name.charAt(i);
                if (c == '$' || c == '_' || c <= 27 || c >= 127) {
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
                } else if ((i == 0 && !isXmlNameStartChar(c)) || (i > 0 && !isXmlNameChar(c))) {
                    result.append(hexPrefix);
                    if (c < 16) result.append("000");
                    else if (c < 256) result.append("00");
                    else if (c < 4096) result.append("0");
                    result.append(Integer.toHexString(c));
                } else {
                    result.append(c);
                }
            }
            s = result.toString();
            escapeCache.put(name, s);
        }
        return s;
    }

    private String decodeName(String name) {
        String s = (String)unescapeCache.get(name);
        if (s == null) {
            final char dollarReplacementFirstChar = dollarReplacement.charAt(0);
            final char escapeReplacementFirstChar = escapeCharReplacement.charAt(0);
            final char hexPrefixFirstChar = hexPrefix.charAt(0);
            final int length = name.length();

            // First, fast (common) case: nothing to decode
            int i = 0;

            for (; i < length; i++ ) {
                char c = name.charAt(i);
                // We'll do a quick check for potential match
                if (c == dollarReplacementFirstChar
                    || c == escapeReplacementFirstChar
                    || c == hexPrefixFirstChar) {
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
                } else if (c == hexPrefixFirstChar && name.startsWith(hexPrefix, i)) {
                    i += hexPrefix.length();
                    c = (char)Integer.parseInt(name.substring(i, i + 4), 16);
                    i += 3;
                    result.append(c);
                } else if (c == escapeReplacementFirstChar
                    && name.startsWith(escapeCharReplacement, i)) {
                    i += escapeCharReplacement.length() - 1;
                    result.append('_');
                } else {
                    result.append(c);
                }
            }

            s = result.toString();
            unescapeCache.put(name, s);
        }
        return s;
    }

    public Object clone() {
        try {
            XmlFriendlyNameCoder coder = (XmlFriendlyNameCoder)super.clone();
            coder.readResolve();
            return coder;

        } catch (CloneNotSupportedException e) {
            throw new ObjectAccessException("Cannot clone XmlFriendlyNameCoder", e);
        }
    }

    private Object readResolve() {
        escapeCache = new WeakCache();
        unescapeCache = new WeakCache();
        return this;
    }

    private static class IntPair {
        int min;
        int max;

        public IntPair(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

    private static boolean isXmlNameStartChar(int cp) {
        return isInNameCharBounds(cp, XML_NAME_START_CHAR_BOUNDS);
    }

    private static boolean isXmlNameChar(int cp) {
        if (isXmlNameStartChar(cp)) {
            return true;
        }
        return isInNameCharBounds(cp, XML_NAME_CHAR_EXTRA_BOUNDS);
    }

    private static boolean isInNameCharBounds(int cp, IntPair[] nameCharBounds) {
        for (int i = 0; i < nameCharBounds.length; ++i) {
            IntPair p = nameCharBounds[i];
            if (cp >= p.min && cp <= p.max) {
                return true;
            }
        }
        return false;
    }
}
