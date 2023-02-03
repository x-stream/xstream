/*
 * Copyright (C) 2014, 2022 XStream Committers.
 * All rights reserved.
 *
 * Created on 09. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

/**
 * Permission for any type with a name matching one of the provided wildcard expressions.
 * <p>
 * Supported are patterns with path expressions using dot as separator:
 * </p>
 * <ul>
 * <li>?: one non-control character except separator, e.g. for 'java.net.Inet?Address'</li>
 * <li>*: arbitrary number of non-control characters except separator, e.g. for types in a package like
 * 'java.lang.*'</li>
 * <li>**: arbitrary number of non-control characters including separator, e.g. for types in a package and subpackages
 * like 'java.lang.**'</li>
 * </ul>
 * <p>
 * The complete range of UTF-8 characters is supported except control characters.
 * </p>
 * <p>
 * Note: The wildcard pattern will not match by default anonymous types, since these classes are rarely used for
 * serialization.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class WildcardTypePermission extends RegExpTypePermission {

    /**
     * Create a WildcardTypePermission.
     * <p>
     * The wildcard pattern will not match anonymous types.
     * </p>
     *
     * @param patterns Array of wildcard patterns for types
     * @since 1.4.7
     */
    public WildcardTypePermission(final String... patterns) {
        this(false, patterns);
    }

    /**
     * Create a WildcardTypePermission.
     *
     * @param allowAnonymous Flag to match also anonymous types with the wildcard
     * @param patterns Array of wildcard patterns for types
     * @since 1.4.20
     */
    public WildcardTypePermission(final boolean allowAnonymous, final String... patterns) {
        super(getRegExpPatterns(allowAnonymous, patterns));
    }

    private static String[] getRegExpPatterns(final boolean allowAnonymous, final String... wildcards) {
        if (wildcards == null) {
            return null;
        }
        final String[] regexps = new String[wildcards.length];
        for (int i = 0; i < wildcards.length; ++i) {
            final String wildcardExpression = wildcards[i];
            final StringBuilder result = new StringBuilder(wildcardExpression.length() * 2);
            result.append("(?u)");
            final int length = wildcardExpression.length();
            for (int j = 0; j < length; j++) {
                final char ch = wildcardExpression.charAt(j);
                switch (ch) {
                case '\\':
                case '.':
                case '+':
                case '|':
                case '[':
                case ']':
                case '(':
                case ')':
                case '^':
                case '$':
                    result.append('\\').append(ch);
                    break;

                case '?':
                    result.append('.');
                    break;

                case '*':
                    // see "General Category Property" in http://www.unicode.org/reports/tr18/
                    if (j + 1 < length && wildcardExpression.charAt(j + 1) == '*') {
                        result.append(allowAnonymous ? "[\\P{C}]*" : "[\\P{C}&&[^$]]*(?:\\$[^0-9$][\\P{C}&&[^.$]]*)*");
                        j++;
                    } else {
                        result.append(allowAnonymous
                            ? "[\\P{C}&&[^.]]*"
                            : "[\\P{C}&&[^.$]]*(?:\\$[^0-9$][\\P{C}&&[^.$]]*)*");
                    }
                    break;

                default:
                    result.append(ch);
                    break;
                }
            }
            regexps[i] = result.toString();
        }
        return regexps;
    }
}
