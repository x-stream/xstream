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
 * @since 1.4.8
 */
public class Types {
    private static final Pattern lambdaPattern = Pattern.compile(".*\\$\\$Lambda(?:\\$[0-9]+|)/.*");

    public static final boolean isLambdaType(final Class<?> type) {
        if (type != null && type.isSynthetic()) {
            String typeName = type.getSimpleName();
            if (typeName.length() == 0) { // JDK >= 17: JDK-8254979 makes getSimpleName() return "" for lambdas
              typeName = type.getName();
            }
            return lambdaPattern.matcher(typeName).matches();
        }
        return false;
    }
}
