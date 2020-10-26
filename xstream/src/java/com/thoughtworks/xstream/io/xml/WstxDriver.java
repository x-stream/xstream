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

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A driver using the Woodstox StAX implementation.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class WstxDriver extends StaxDriver {

    public WstxDriver() {
        super();
    }

    /**
     * @deprecated As of 1.4.6 use {@link #WstxDriver(QNameMap, NameCoder)}
     */
    @Deprecated
    public WstxDriver(final QNameMap qnameMap, final XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public WstxDriver(final QNameMap qnameMap, final NameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public WstxDriver(final QNameMap qnameMap) {
        super(qnameMap);
    }

    /**
     * @deprecated As of 1.4.6 use {@link #WstxDriver(NameCoder)}
     */
    @Deprecated
    public WstxDriver(final XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public WstxDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    @Override
    protected XMLInputFactory createInputFactory() {
        final XMLInputFactory instance = new WstxInputFactory();
        instance.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return instance;
    }

    @Override
    protected XMLOutputFactory createOutputFactory() {
        return new WstxOutputFactory();
    }

}
