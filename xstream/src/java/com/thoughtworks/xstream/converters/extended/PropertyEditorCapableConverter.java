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

package com.thoughtworks.xstream.converters.extended;

import java.beans.PropertyEditor;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.core.util.ThreadSafePropertyEditor;


/**
 * A SingleValueConverter that can utilize a {@link PropertyEditor} implementation used for a specific type. The
 * converter ensures that the editors can be used concurrently.
 * 
 * @author Jukka Lindstr&ouml;m
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class PropertyEditorCapableConverter implements SingleValueConverter {

    private final ThreadSafePropertyEditor editor;
    private final Class<?> type;

    public PropertyEditorCapableConverter(final Class<? extends PropertyEditor> propertyEditorType, final Class<?> type) {
        this.type = type;
        editor = new ThreadSafePropertyEditor(propertyEditorType, 2, 5);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return this.type == type;
    }

    @Override
    public Object fromString(final String str) {
        return editor.setAsText(str);
    }

    @Override
    public String toString(final Object obj) {
        return editor.getAsText(obj);
    }

}
