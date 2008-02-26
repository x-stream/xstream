/*
 * Copyright (C) 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 20.09.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.core.util.ThreadSafePropertyEditor;

import java.beans.PropertyEditor;


/**
 * A SingleValueConverter that can utilize a {@link PropertyEditor} implementation used for a
 * specific type. The converter ensures that the editors can be used concurrently.
 * 
 * @author Jukka Lindstr&ouml;m
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class PropertyEditorCapableConverter implements SingleValueConverter {

    private final ThreadSafePropertyEditor editor;
    private final Class type;

    public PropertyEditorCapableConverter(final Class propertyEditorType, final Class type) {
        this.type = type;
        editor = new ThreadSafePropertyEditor(propertyEditorType, 2, 5);
    }

    public boolean canConvert(final Class type) {
        return this.type == type;
    }

    public Object fromString(final String str) {
        return editor.setAsText(str);
    }

    public String toString(final Object obj) {
        return editor.getAsText(obj);
    }

}
