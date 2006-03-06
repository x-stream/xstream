package com.thoughtworks.xstream.core;

import junit.framework.TestCase;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.mapper.DefaultMapper;

/**
 * @author Guilherme Silveira
 */
public class DefaultConverterLookupTest extends TestCase {

	public void testConverterChange() {
		final DefaultConverterLookup lookup = new DefaultConverterLookup(
				new DefaultMapper(Thread.currentThread()
						.getContextClassLoader()));
		Converter currentConverter = new SingleValueConverterWrapper(new StringConverter());
		lookup.registerConverter(currentConverter, -100);
		assertEquals(lookup.lookupConverterForType(String.class), currentConverter);
		Converter newConverter = new SingleValueConverterWrapper(new StringConverter());
		lookup.registerConverter(newConverter, 100);
		assertEquals(lookup.lookupConverterForType(String.class), newConverter);
	}

}
