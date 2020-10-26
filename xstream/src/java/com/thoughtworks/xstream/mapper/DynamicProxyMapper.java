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

package com.thoughtworks.xstream.mapper;

import java.lang.reflect.Proxy;


/**
 * Mapper for handling special cases of aliasing dynamic proxies.
 * <p>
 * The alias property specifies the name an instance of a dynamic proxy should be serialized with.
 * </p>
 * 
 * @author Joe Walnes
 */
public class DynamicProxyMapper extends MapperWrapper {

    private String alias;

    public DynamicProxyMapper(final Mapper wrapped) {
        this(wrapped, "dynamic-proxy");
    }

    public DynamicProxyMapper(final Mapper wrapped, final String alias) {
        super(wrapped);
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

    @Override
    public String serializedClass(final Class<?> type) {
        if (Proxy.isProxyClass(type)) {
            return alias;
        } else {
            return super.serializedClass(type);
        }
    }

    @Override
    public Class<?> realClass(final String elementName) {
        if (elementName.equals(alias)) {
            return DynamicProxy.class;
        } else {
            return super.realClass(elementName);
        }
    }

    /**
     * Place holder type used for dynamic proxies.
     */
    public static class DynamicProxy {}

}
