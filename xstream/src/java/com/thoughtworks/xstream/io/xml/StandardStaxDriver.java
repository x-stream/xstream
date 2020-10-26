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

package com.thoughtworks.xstream.io.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A driver using the standard JDK StAX implementation provided by the Java runtime.
 * <p>
 * In contrast to XMLInputFactory.newFactory() or XMLOutputFactory.newFactory() this implementation will ignore any
 * implementations provided with the system properties <em>javax.xml.stream.XMLInputFactory</em> and
 * <em>javax.xml.stream.XMLOutputFactory</em>, all implementations configured in <em>lib/stax.properties</em> or
 * registered with the Service API.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.5
 */
public class StandardStaxDriver extends StaxDriver {

    public StandardStaxDriver() {
        super();
    }

    /**
     * @deprecated As of 1.4.6 use {@link #StandardStaxDriver(QNameMap, NameCoder)}
     */
    @Deprecated
    public StandardStaxDriver(final QNameMap qnameMap, final XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public StandardStaxDriver(final QNameMap qnameMap, final NameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public StandardStaxDriver(final QNameMap qnameMap) {
        super(qnameMap);
    }

    /**
     * @deprecated As of 1.4.6 use {@link #StandardStaxDriver(NameCoder)}
     */
    @Deprecated
    public StandardStaxDriver(final XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public StandardStaxDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    @Override
    protected XMLInputFactory createInputFactory() {
        Exception exception = null;
        try {
            final Class<? extends XMLInputFactory> staxInputFactory = JVM.getStaxInputFactory();
            if (staxInputFactory != null) {
                final XMLInputFactory instance = staxInputFactory.newInstance();
                instance.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
                return instance;
            } else {
                throw new StreamException("Java runtime has no standard XMLInputFactory implementation.", exception);
            }
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create standard XMLInputFactory instance of Java runtime.", exception);
    }

    @Override
    protected XMLOutputFactory createOutputFactory() {
        Exception exception = null;
        try {
            final Class<? extends XMLOutputFactory> staxOutputFactory = JVM.getStaxOutputFactory();
            if (staxOutputFactory != null) {
                return staxOutputFactory.newInstance();
            } else {
                throw new StreamException("Java runtime has no standard XMLOutputFactory implementation.", exception);
            }
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create standard XMLOutputFactory instance of Java runtime.", exception);
    }

}
