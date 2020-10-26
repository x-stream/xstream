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

package com.thoughtworks.xstream.core;

import java.util.BitSet;

import junit.framework.TestCase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.collections.BitSetConverter;

/**
 * @author Guilherme Silveira
 */
public class DefaultConverterLookupTest extends TestCase {

	public void testCanReplaceWithHigherPriority() {
		
		// this test actually depends on the keyset implementation of the corresponding cache map.
		final DefaultConverterLookup lookup = new DefaultConverterLookup();
		Converter currentConverter = new SingleValueConverterWrapper(new StringConverter());
		lookup.registerConverter(new BitSetConverter(), XStream.PRIORITY_VERY_HIGH);
		lookup.registerConverter(currentConverter, -100);
		lookup.lookupConverterForType(String.class);
		lookup.lookupConverterForType(BitSet.class);
		assertEquals(lookup.lookupConverterForType(String.class), currentConverter);
		Converter newConverter = new SingleValueConverterWrapper(new StringConverter());
		lookup.registerConverter(newConverter, 100);
		assertEquals(lookup.lookupConverterForType(String.class), newConverter);
	}

}
