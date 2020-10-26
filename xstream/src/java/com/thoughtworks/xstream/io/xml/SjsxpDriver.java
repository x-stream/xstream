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

import com.thoughtworks.xstream.io.StreamException;


/**
 * A driver using the JDK 6 StAX implementation of Sun.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 * @deprecated As of 1.4.5 use {@link StandardStaxDriver}
 */
@Deprecated
public class SjsxpDriver extends StaxDriver {

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#StandardStaxDriver()}
     */
    @Deprecated
    public SjsxpDriver() {
        super();
    }

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#StandardStaxDriver(QNameMap, XmlFriendlyNameCoder)}
     */
    @Deprecated
    public SjsxpDriver(final QNameMap qnameMap, final XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#StandardStaxDriver(QNameMap)}
     */
    @Deprecated
    public SjsxpDriver(final QNameMap qnameMap) {
        super(qnameMap);
    }

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#StandardStaxDriver(XmlFriendlyNameCoder)}
     */
    @Deprecated
    public SjsxpDriver(final XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#createInputFactory()}
     */
    @Deprecated
    @Override
    protected XMLInputFactory createInputFactory() {
        Exception exception = null;
        try {
            final XMLInputFactory instance = (XMLInputFactory)Class.forName("com.sun.xml.internal.stream.XMLInputFactoryImpl").newInstance();
            instance.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            return instance;
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create SJSXP (Sun JDK 6 StAX) XMLInputFactory instance.", exception);
    }

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#createOutputFactory()}
     */
    @Deprecated
    @Override
    protected XMLOutputFactory createOutputFactory() {
        Exception exception = null;
        try {
            return (XMLOutputFactory)Class.forName("com.sun.xml.internal.stream.XMLOutputFactoryImpl").newInstance();
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create SJSXP (Sun JDK 6 StAX) XMLOutputFactory instance.", exception);
    }

}
