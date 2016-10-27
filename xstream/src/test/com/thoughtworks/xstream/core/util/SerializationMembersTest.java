package com.thoughtworks.xstream.core.util;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;

/**
 *
 */
public class SerializationMembersTest extends TestCase {

	public static class Delegate1 extends ArrayList<Object> {
		private static final long serialVersionUID = 1L;
		private List<Object> delegate2;

		public Delegate1(List<Object> aDelegate2) {
			super();
			delegate2 = aDelegate2;
		}

		public Object writeReplace() {
			return delegate2;
		}
	}

	public static class Delegate2 extends ArrayList<Object> {
		private static final long serialVersionUID = 1L;
		private List<Object> delegate3;

		public Delegate2(List<Object> aDelegate3) {
			super();
			delegate3 = aDelegate3;
		}

		public Object writeReplace() {
			return delegate3;
		}
	}

	public void testDuobleDelegate() {
		List<Object> tempList3 = new ArrayList<Object>();
		List<Object> tempList2 = new Delegate2(tempList3);
		List<Object> tempList1 = new Delegate1(tempList2);
		XStream tempXStream = new XStream();
		String tempXml = tempXStream.toXML(tempList1);
		List<Object> tempXStreamCopy = (List<Object>) tempXStream.fromXML(tempXml);
		assertEquals(ArrayList.class, tempXStreamCopy.getClass());
	}

}
