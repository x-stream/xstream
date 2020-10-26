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

import org.hibernate.proxy.HibernateProxy;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converter for Hibernate proxy instances. The converter will effectively remove any trace of the proxy.
 *
 * @author Konstantin Pribluda
 * @author J&ouml;rg Schaible
 */
public class HibernateProxyConverter implements Converter {
    @Override
    public boolean canConvert(final Class<?> clazz) {
        // be responsible for Hibernate proxy.
        return clazz != null && HibernateProxy.class.isAssignableFrom(clazz);
    }

    @Override
    public void marshal(final Object object, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Object item = ((HibernateProxy)object).getHibernateLazyInitializer().getImplementation();
        context.convertAnother(item);
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        throw new ConversionException("Cannot deserialize Hibernate proxy");
    }
}
