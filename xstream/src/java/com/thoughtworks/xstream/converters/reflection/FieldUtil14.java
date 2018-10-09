/*
 * Copyright (C) 2018 XStream Committers.
 * All rights reserved.
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * Created on 10. October 2018 by Joerg Schaible.
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;

/**
 * @author J&ouml;rg Schaible
 */
class FieldUtil14 implements FieldDictionary.FieldUtil {

    public boolean isSynthetic(final Field field) {
        return false;
    }
}
