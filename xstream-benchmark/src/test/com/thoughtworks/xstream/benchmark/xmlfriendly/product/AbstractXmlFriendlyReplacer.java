/*
 * Copyright (C) 2007 XStream Committers.
 * Created on 13.09.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.xmlfriendly.product;

import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;


/**
 * Abstract base class for the XmlFriendlyReplacer with all kind of implementations.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public abstract class AbstractXmlFriendlyReplacer extends XmlFriendlyReplacer {

    private final String dollarReplacement;
    private final String underscoreReplacement;
    private final int bufferIncrement;

    /**
     * Creates an XmlFriendlyReplacer with custom replacements
     * 
     * @param dollarReplacement the replacement for '$'
     * @param underscoreReplacement the replacement for '_'
     * @param bufferIncrement buffer increment for preallocation
     */
    public AbstractXmlFriendlyReplacer(
        String dollarReplacement, String underscoreReplacement, int bufferIncrement) {
        this.dollarReplacement = dollarReplacement;
        this.underscoreReplacement = underscoreReplacement;
        this.bufferIncrement = bufferIncrement;
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

    protected String escapeNoName(String name) {
        return name;
    }

    protected String unescapeNoName(String name) {
        return name;
    }

    protected String escapeIterativelyAppending(String name) {
        int length = name.length();
        StringBuffer result = new StringBuffer(length + bufferIncrement);
        for (int i = 0; i < length; i++) {
            char c = name.charAt(i);
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

    protected String unescapeIterativelyAppending(String name) {
        int underscoreReplacementInc = underscoreReplacement.length() - 1;
        int dollarReplacementInc = dollarReplacement.length() - 1;
        int length = name.length();
        StringBuffer result = new StringBuffer(length + bufferIncrement);
        for (int i = 0; i < length; i++) {
            char c = name.charAt(i);
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

    protected String escapeByCombinedLookupAppending(String name) {
        int length = name.length();
        StringBuffer result = new StringBuffer(length + bufferIncrement);
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

    protected String unescapeByCombinedLookupAppending(String name) {
        int underscoreReplacementLength = underscoreReplacement.length();
        int dollarReplacementLength = dollarReplacement.length();
        int length = name.length();
        StringBuffer result = new StringBuffer(length + bufferIncrement);
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

    protected String escapeByCombinedLookupReplacing(String name) {
        int underscoreReplacementLength = underscoreReplacement.length();
        int dollarReplacementLength = dollarReplacement.length();
        StringBuffer result = new StringBuffer(name.length() + bufferIncrement);
        result.append(name);
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

    protected String unescapeByCombinedLookupReplacing(String name) {
        int underscoreReplacementLength = underscoreReplacement.length();
        int dollarReplacementLength = dollarReplacement.length();
        StringBuffer result = new StringBuffer(name.length() + bufferIncrement);
        result.append(name);
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

    protected String escapeBySeparateLookupReplacing(String name) {
        StringBuffer result = new StringBuffer(name);
        int underscoreReplacementInc = underscoreReplacement.length();
        int dollarReplacementInc = dollarReplacement.length();
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

    protected String unescapeBySeparateLookupReplacing(String name) {
        StringBuffer result = new StringBuffer(name);
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

    protected String escapeIterativelyReplacing(String name) {
        int length = name.length();
        int underscoreReplacementInc = underscoreReplacement.length() - 1;
        int dollarReplacementInc = dollarReplacement.length() - 1;
        StringBuffer result = new StringBuffer(length + bufferIncrement);
        result.append(name);
        for (int i = 0; i < length; i++) {
            char c = result.charAt(i);
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

    protected String unescapeIterativelyReplacing(String name) {
        char dollarChar = dollarReplacement.charAt(0);
        char underscoreChar = underscoreReplacement.charAt(0);
        int underscoreReplacementLength = underscoreReplacement.length();
        int dollarReplacementLength = dollarReplacement.length();
        int length = name.length();
        StringBuffer result = new StringBuffer(length + bufferIncrement);
        result.append(name);
        for (int i = 0; i < length; ++i) {
            char c = result.charAt(i);
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

}
