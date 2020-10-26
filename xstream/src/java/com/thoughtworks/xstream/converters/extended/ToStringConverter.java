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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;


/**
 * Convenient converter for classes with natural string representation.
 * <p>
 * Converter for classes that adopt the following convention: - a constructor that takes a single string parameter - a
 * toString() that is overloaded to issue a string that is meaningful
 * </p>
 *
 * @author Paul Hammant
 */
public class ToStringConverter extends AbstractSingleValueConverter {
    private final Class<?> clazz;
    private final Constructor<?> ctor;

    public ToStringConverter(final Class<?> clazz) throws NoSuchMethodException {
        this.clazz = clazz;
        ctor = clazz.getConstructor(String.class);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == clazz;
    }

    @Override
    public String toString(final Object obj) {
        return obj == null ? null : obj.toString();
    }

    @Override
    public Object fromString(final String str) {
        try {
            return ctor.newInstance(str);
        } catch (final InstantiationException e) {
            throw new ConversionException("Unable to instantiate single String param constructor", e);
        } catch (final IllegalAccessException e) {
            throw new ObjectAccessException("Unable to access single String param constructor", e);
        } catch (final InvocationTargetException e) {
            throw new ConversionException("Unable to target single String param constructor", e.getTargetException());
        }
    }
}
