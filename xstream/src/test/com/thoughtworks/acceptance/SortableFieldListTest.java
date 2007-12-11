/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 10. April 2007 by Guilherme Silveira
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SortableFieldKeySorter;

public class SortableFieldListTest extends AbstractAcceptanceTest {

	public void testSortsFieldOrderWithArray() {

		SortableFieldKeySorter sorter = new SortableFieldKeySorter();
		sorter.registerFieldOrder(MommyBear.class,
				new String[] { "b", "c", "a" });

		xstream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(sorter)));

		xstream.alias("mommy", MommyBear.class);
		MommyBear root = new MommyBear();
		root.c = "ccc";
		root.b = "bbb";
		root.a = "aaa";
		assertBothWays(root, "<mommy>\n" + "  <b>bbb</b>\n" + "  <c>ccc</c>\n"
				+ "  <a>aaa</a>\n" + "</mommy>");
	}

	public void testSortsFieldOrderWhileUsingInheritance() {

		SortableFieldKeySorter sorter = new SortableFieldKeySorter();
		sorter.registerFieldOrder(BabyBear.class,
				new String[] { "b", "d", "c", "a" });

		xstream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(sorter)));

		xstream.alias("baby", BabyBear.class);
		BabyBear root = new BabyBear();
		root.c = "ccc";
		root.b = "bbb";
		root.a = "aaa";
		root.d = "ddd";
		assertBothWays(root, "<baby>\n" + "  <b>bbb</b>\n" + "  <d>ddd</d>\n"
				+ "  <c>ccc</c>\n" + "  <a>aaa</a>\n" + "</baby>");
	}

	public static class MommyBear {
		protected String c, a, b;
	}

	public static class BabyBear extends MommyBear {
		private String d;
	}

}
