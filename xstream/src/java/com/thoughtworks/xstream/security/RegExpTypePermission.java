/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

    public RegExpTypePermission(final String... patterns) {
        this(getPatterns(patterns));
    }

    public RegExpTypePermission(final Pattern... patterns) {
        this.patterns = patterns == null ? new Pattern[0] : patterns;
    }

    @Override
    public boolean allows(final Class<?> type) {
        if (type != null) {
            final String name = type.getName();
            for (final Pattern pattern : patterns)
                if (pattern.matcher(name).matches())
                    return true;
        }
        return false;
    }

    private static Pattern[] getPatterns(final String... patterns) {
        if (patterns == null)
            return null;
        final Pattern[] array = new Pattern[patterns.length];
        for (int i = 0; i < array.length; ++i)
            array[i] = Pattern.compile(patterns[i]);
        return array;
    }
}
