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

package com.thoughtworks.xstream.core.util;

import java.util.regex.Pattern;


/**
 * Helper methods for class types.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.8
 */
public class Types {
    private static final Pattern lambdaPattern = Pattern.compile(".*\\$\\$Lambda\\$[0-9]+/.*");

    public static final boolean isLambdaType(final Class<?> type) {
        return type != null && type.isSynthetic() && lambdaPattern.matcher(type.getSimpleName()).matches();
    }

}
