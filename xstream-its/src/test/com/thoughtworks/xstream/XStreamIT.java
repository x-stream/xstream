/*
 * Copyright (C) 2003, 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream;

import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class XStreamIT
{
  @Inject
  BundleContext bundleContext;

  @Configuration
  public Option[] config() throws Exception {
    Properties properties = new Properties();
    try (InputStream is = getClass().getResourceAsStream("/project.properties")) {
      properties.load(is);
    }
    String xstreamVersion = properties.getProperty("project.version");

    return options(
        junitBundles(),
        mavenBundle().groupId("com.thoughtworks.xstream").artifactId("xstream").version(xstreamVersion)
    );
  }

  @Test
  public void smokeTest() {
    assertNotNull("BundleContext was not injected", bundleContext);

    boolean xstreamBundleFound = false;
    int xstreamBundleState = -1;
    for (Bundle bundle : bundleContext.getBundles()) {
      if ("xstream".equals(bundle.getSymbolicName())) {
        xstreamBundleFound = true;
        xstreamBundleState = bundle.getState();
      }
    }

    assertTrue("XStream bundle was not loaded", xstreamBundleFound);
    assertEquals(
        format("XStream bundle was not active (was: %d)", xstreamBundleState),
        xstreamBundleState, Bundle.ACTIVE
    );
  }
}
