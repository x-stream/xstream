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

package com.thoughtworks.xstream.hibernate.converter;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.hibernate.util.Hibernate;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * A converter for Hibernate's PersistentBag, PersistentList and PersistentSet and for ListProxy and SetProxy from
 * Hibernate's Envers add-on. The converter will drop any reference to the Hibernate collection and emit at
 * serialization time an equivalent JDK collection instead.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class HibernatePersistentCollectionConverter extends CollectionConverter {

    /**
     * Construct a HibernatePersistentCollectionConverter.
     *
     * @param mapper
     * @since 1.4
     */
    public HibernatePersistentCollectionConverter(final Mapper mapper) {
        super(mapper);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null
            && (type == Hibernate.PersistentBag
                || type == Hibernate.PersistentList
                || type == Hibernate.PersistentSet
                || type == Hibernate.EnversList || type == Hibernate.EnversSet);
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        throw new ConversionException("Cannot deserialize Hibernate collection");
    }
}
