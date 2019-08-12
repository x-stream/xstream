/*
 * Copyright (C) 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. August 2019 by Joerg Schaible
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
        final InputStream is = getClass().getResourceAsStream("/project.properties");
        properties.load(is);
        is.close();
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
