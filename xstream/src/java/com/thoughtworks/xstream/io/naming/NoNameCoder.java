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

package com.thoughtworks.xstream.io.naming;

/**
 * A NameCoder that does nothing.
 * <p>
 * The usage of this implementation implies that the names used for the objects can also be used in the target format
 * without any change. This applies also for XML if the object graph contains no object that is an instance of an inner
 * class type or is in the default package.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class NoNameCoder implements NameCoder {

    @Override
    public String decodeAttribute(final String attributeName) {
        return attributeName;
    }

    @Override
    public String decodeNode(final String nodeName) {
        return nodeName;
    }

    @Override
    public String encodeAttribute(final String name) {
        return name;
    }

    @Override
    public String encodeNode(final String name) {
        return name;
    }

}
