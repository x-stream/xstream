/*
 * Copyright (C) 2015 XStream Committers.
 * All rights reserved.
 *
 * Created on 17. January 2015 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.util.regex.Pattern;


/**
 * Helper methods for class types.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class Types {
    private static final Pattern lambdaPattern = Pattern.compile(".*\\$\\$Lambda\\$[0-9]+/.*");

    public static final boolean isLambdaType(final Class<?> type) {
        return type != null && type.isSynthetic() && lambdaPattern.matcher(type.getSimpleName()).matches();
    }

}
