/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 09. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

import java.util.regex.Pattern;


/**
 * Permission for any type with a name matching one of the provided regular expressions.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class RegExpTypePermission implements TypePermission {

    private final Pattern[] patterns;

    public RegExpTypePermission(final String[] patterns) {
        this(getPatterns(patterns));
    }

    public RegExpTypePermission(final Pattern[] patterns) {
        this.patterns = patterns == null ? new Pattern[0] : patterns;
    }

    public boolean allows(final Class type) {
        if (type != null) {
            final String name = type.getName();
            for (int i = 0; i < patterns.length; ++i)
                if (patterns[i].matcher(name).matches())
                    return true;
        }
        return false;
    }

    private static Pattern[] getPatterns(final String[] patterns) {
        if (patterns == null)
            return null;
        final Pattern[] array = new Pattern[patterns.length];
        for (int i = 0; i < array.length; ++i)
            array[i] = Pattern.compile(patterns[i]);
        return array;
    }
}
