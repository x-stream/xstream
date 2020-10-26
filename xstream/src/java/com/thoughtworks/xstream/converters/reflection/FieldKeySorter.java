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

package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.util.Map;


/**
 * An interface capable of sorting fields.
 * <p>
 * Implement this interface if you want to customize the field order in which XStream serializes objects.
 * </p>
 * 
 * @author Guilherme Silveira
 * @since 1.2.2
 */
public interface FieldKeySorter {

    /**
     * Sort the fields of a type.
     * <p>
     * The method will be called with the class type that contains all the fields and a Map that retains the order in
     * which the elements have been added. The sequence in which elements are returned by an iterator defines the
     * processing order of the fields. An implementation may create a different Map with similar semantic, add all
     * elements of the original map and return the new one.
     * </p>
     * 
     * @param type the class that contains all the fields
     * @param keyedByFieldKey a Map containing a {@link FieldKey} as key element and a {@link java.lang.reflect.Field}
     *            as value.
     * @return a Map with all the entries of the original Map
     * @since 1.2.2
     */
    Map<FieldKey, Field> sort(Class<?> type, Map<FieldKey, Field> keyedByFieldKey);

}
