/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2005 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.thoughtworks.xstream.mapper.CannotResolveClassException;


public class ClassLoaderTest extends AbstractAcceptanceTest {

    public void testAllowsClassLoaderToBeOverriden() throws MalformedURLException {
        String name = "com.thoughtworks.proxy.kit.SimpleReference";
        String xml = "<com.thoughtworks.proxy.kit.SimpleReference/>";
        xstream.allowTypes(new String[]{name});
        try {
            xstream.fromXML(xml);
            fail("Thrown " + CannotResolveClassException.class.getName() + " expected");
        } catch (final CannotResolveClassException e) {
            assertEquals(name, e.getMessage());
        }
        
        File proxyToys = new File("target/lib/proxytoys-0.2.1.jar");
        ClassLoader classLoader = new URLClassLoader(
            new URL[]{proxyToys.toURI().toURL()}, getClass().getClassLoader());
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
        xstream.allowTypes(new String[]{name});
        assertEquals(name, xstream.fromXML(xml).getClass().getName());
        
        xstream = createXStream();
        xstream.allowTypes(new String[]{name});
        try {
            xstream.fromXML(xml);
            fail("Thrown " + CannotResolveClassException.class.getName() + " expected");
        } catch (final CannotResolveClassException e) {
            assertEquals(name, e.getMessage());
        }
    }
}
