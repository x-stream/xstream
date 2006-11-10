package com.thoughtworks.xstream.core;

import java.util.BitSet;

import junit.framework.TestCase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.collections.BitSetConverter;
import com.thoughtworks.xstream.mapper.DefaultMapper;

/**
 * @author Guilherme Silveira
 */
public class DefaultConverterLookupTest extends TestCase {

	public void testConverterChange() {
		
		// this test actually depends on the keyset implementation of the corresponding cache map.
		final DefaultConverterLookup lookup = new DefaultConverterLookup(
				new DefaultMapper(Thread.currentThread()
						.getContextClassLoader()));
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
