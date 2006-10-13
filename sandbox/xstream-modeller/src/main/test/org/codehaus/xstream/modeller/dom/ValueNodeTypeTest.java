package org.codehaus.xstream.modeller.dom;

import org.codehaus.xstream.modeller.AbstractTestCase;

public class ValueNodeTypeTest extends AbstractTestCase {

	public void testOverridesLongWithDouble() {
		Value type = new Value("lv");
		type.checkType("127");
		type.checkType("127.5");
		assertEquals("double", type.getType());
	}

	public void testOverridesDoubleWithLong() {
		Value type = new Value("dv");
		type.checkType("127.5");
		type.checkType("127");
		assertEquals("double", type.getType());
	}

	public void testOverridesLongWithString() {
		Value type = new Value("sv");
		type.checkType("127");
		type.checkType("127a");
		assertEquals("String", type.getType());
	}

}
