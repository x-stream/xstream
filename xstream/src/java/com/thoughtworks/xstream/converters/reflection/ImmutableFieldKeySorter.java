/*
 * Copyright (C) 2007, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 10. April 2007 by Guilherme Silveira
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.util.Map;


/**
 * Does not change the order of the fields.
 * 
 * @author Guilherme Silveira
 * @since 1.2.2
 */
public class ImmutableFieldKeySorter implements FieldKeySorter {

    @Override
    public Map<FieldKey, Field> sort(final Class<?> type, final Map<FieldKey, Field> keyedByFieldKey) {
        return keyedByFieldKey;
    }

}
