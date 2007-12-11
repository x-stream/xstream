/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24. July 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import java.sql.Time;

/**
 * Converts a java.sql.Time to text. Warning: Any granularity smaller than seconds is lost.
 *
 * @author Jose A. Illescas
 */
public class SqlTimeConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(Time.class);
    }

    public Object fromString(String str) {
        return Time.valueOf(str);
    }

}
