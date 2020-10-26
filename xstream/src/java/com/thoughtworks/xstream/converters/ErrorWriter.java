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

package com.thoughtworks.xstream.converters;

import java.util.Iterator;


/**
 * To aid debugging, some components are passed an ErrorWriter when things go wrong, allowing them to add information to
 * the error message that may be helpful to diagnose problems.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public interface ErrorWriter {

    /**
     * Add some information to the error message. The information will be added even if the identifier is already in
     * use.
     * 
     * @param name something to identify the type of information (e.g. 'XPath').
     * @param information detail of the message (e.g. '/blah/moo[3]'
     */
    void add(String name, String information);

    /**
     * Set some information to the error message. If the identifier is already in use, the new information will replace
     * the old one.
     * 
     * @param name something to identify the type of information (e.g. 'XPath').
     * @param information detail of the message (e.g. '/blah/moo[3]'
     * @since 1.4
     */
    void set(String name, String information);

    /**
     * Retrieve information of the error message.
     * 
     * @param errorKey the key of the message
     * @return the value
     * @since 1.3
     */
    String get(String errorKey);

    /**
     * Retrieve an iterator over all keys of the error message.
     * 
     * @return an Iterator
     * @since 1.3
     */
    Iterator<String> keys();
}
