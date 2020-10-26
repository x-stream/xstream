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

package com.thoughtworks.acceptance;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.thoughtworks.xstream.mapper.CannotResolveClassException;


public class ClassLoaderTest extends AbstractAcceptanceTest {

    public void testAllowsClassLoaderToBeOverriden() throws MalformedURLException {
        final String name = "com.thoughtworks.proxy.kit.SimpleReference";
        final String xml = "<com.thoughtworks.proxy.kit.SimpleReference/>";
        xstream.allowTypes(name);
        try {
            xstream.fromXML(xml);
            fail("Thrown " + CannotResolveClassException.class.getName() + " expected");
        } catch (final CannotResolveClassException e) {
            assertEquals(name, e.getMessage());
        }

        final File proxyToys = new File("target/lib/proxytoys-0.2.1.jar");
        @SuppressWarnings("resource")
        final ClassLoader classLoader = new URLClassLoader(new URL[]{proxyToys.toURI().toURL()}, getClass()
            .getClassLoader());
        // will not work, since class has already been cached
        xstream.setClassLoader(classLoader);

        try {
            xstream.fromXML(xml);
            fail("Thrown " + CannotResolveClassException.class.getName() + " expected");
        } catch (final CannotResolveClassException e) {
            assertEquals(name, e.getMessage());
        }

        xstream = createXStream();
        xstream.setClassLoader(classLoader);
        xstream.allowTypes(name);
        assertEquals(name, xstream.fromXML(xml).getClass().getName());

        xstream = createXStream();
        xstream.allowTypes(name);
        try {
            xstream.fromXML(xml);
            fail("Thrown " + CannotResolveClassException.class.getName() + " expected");
        } catch (final CannotResolveClassException e) {
            assertEquals(name, e.getMessage());
        }
    }
}
