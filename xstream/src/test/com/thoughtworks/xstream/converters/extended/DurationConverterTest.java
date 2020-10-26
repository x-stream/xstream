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

package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.SingleValueConverter;

import junit.framework.TestCase;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;


/**
 * @author John Kristian
 */
public class DurationConverterTest extends TestCase {
    private static final String[] STRINGS = {"-P1Y2M3DT4H5M6.7S", "P1Y", "PT1H2M"};

    public void testConversion() throws Exception {
        final SingleValueConverter converter = new DurationConverter();
        DatatypeFactory factory = DatatypeFactory.newInstance();
	for (String s : STRINGS) {
	    Duration o = factory.newDuration(s);
	    assertEquals(s, converter.toString(o));
	    assertEquals(o, converter.fromString(s));
	}
    }

}
