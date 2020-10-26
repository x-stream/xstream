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

package com.thoughtworks.xstream;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author Wes Wannemacher
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OSGiIT {
    @Inject
    BundleContext bundleContext;

    @SuppressWarnings("javadoc")
    @Configuration
    public Option[] config() throws Exception {
        final Properties properties = new Properties();
        try (final InputStream is = getClass().getResourceAsStream("/project.properties")) {
            properties.load(is);
        }
        final String xstreamVersion = properties.getProperty("project.version");

        return options(junitBundles(), mavenBundle()
            .groupId("com.thoughtworks.xstream")
            .artifactId("xstream")
            .version(xstreamVersion));
    }

    @SuppressWarnings("javadoc")
    @Test
    public void smokeTest() {
        assertNotNull("BundleContext was not injected", bundleContext);

        boolean xstreamBundleFound = false;
        int xstreamBundleState = -1;
        for (final Bundle bundle : bundleContext.getBundles()) {
            if ("xstream".equals(bundle.getSymbolicName())) {
                xstreamBundleFound = true;
                xstreamBundleState = bundle.getState();
            }
        }

        assertTrue("XStream bundle was not loaded", xstreamBundleFound);
        assertEquals(format("XStream bundle was not active (was: %d)", xstreamBundleState), xstreamBundleState,
            Bundle.ACTIVE);
    }
}
