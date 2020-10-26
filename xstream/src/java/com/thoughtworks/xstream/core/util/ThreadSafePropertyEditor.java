/*
 * Copyright (c) 2007, 2020 Oracle and/or its affiliates. All rights reserved.
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

import java.beans.PropertyEditor;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.ErrorWritingException;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;


/**
 * Wrapper around {@link PropertyEditor} that can be called by multiple threads concurrently.
 * <p>
 * A PropertyEditor is not thread safe. To make best use of resources, the PropertyEditor provides a dynamically sizing
 * pool of instances, each of which will only be called by a single thread at a time.
 * </p>
 * <p>
 * The pool has a maximum capacity, to limit overhead. If all instances in the pool are in use and another is required,
 * it shall block until one becomes available.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class ThreadSafePropertyEditor {

    private final Class<? extends PropertyEditor> editorType;
    private final Pool<PropertyEditor> pool;

    public ThreadSafePropertyEditor(
            final Class<? extends PropertyEditor> type, final int initialPoolSize, final int maxPoolSize) {
        if (!PropertyEditor.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not a " + PropertyEditor.class.getName());
        }
        editorType = type;
        pool = new Pool<PropertyEditor>(initialPoolSize, maxPoolSize, new Pool.Factory<PropertyEditor>() {
            @Override
            public PropertyEditor newInstance() {
                ErrorWritingException ex = null;
                try {
                    return editorType.newInstance();
                } catch (final InstantiationException e) {
                    ex = new ConversionException("Faild to call default constructor", e);
                } catch (final IllegalAccessException e) {
                    ex = new ObjectAccessException("Cannot call default constructor", e);
                }
                ex.add("construction-type", editorType.getName());
                throw ex;
            }

        });
    }

    public String getAsText(final Object object) {
        final PropertyEditor editor = fetchFromPool();
        try {
            editor.setValue(object);
            return editor.getAsText();
        } finally {
            pool.putInPool(editor);
        }
    }

    public Object setAsText(final String str) {
        final PropertyEditor editor = fetchFromPool();
        try {
            editor.setAsText(str);
            return editor.getValue();
        } finally {
            pool.putInPool(editor);
        }
    }

    private PropertyEditor fetchFromPool() {
        return pool.fetchFromPool();
    }
}
