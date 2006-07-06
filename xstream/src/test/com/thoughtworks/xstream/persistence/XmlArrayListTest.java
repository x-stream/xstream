package com.thoughtworks.xstream.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class XmlArrayListTest extends TestCase {
	private MockedStrategy strategy;

	public void setUp() throws Exception {
		super.setUp();
		strategy = new MockedStrategy();
	}

	public void testWritesASingleObject() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		assertTrue(strategy.map.containsValue("guilherme"));
	}

	public void testWritesASingleObjectInANegativePosition() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		try {
			xmlList.add(-1, "guilherme");
			fail();
		} catch (IndexOutOfBoundsException ex) {
			// ok
		}
	}

	public void testWritesASingleObjectInFirstPosition() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		assertTrue(strategy.map.containsKey("0"));
	}

	public void testWritesTwoObjects() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		xmlList.add("silveira");
		assertTrue(strategy.map.containsValue("guilherme"));
		assertTrue(strategy.map.containsValue("silveira"));
		assertTrue(strategy.map.containsKey("0"));
		assertTrue(strategy.map.containsKey("1"));
	}

	public void testWritesTwoObjectsGuaranteesItsEnumerationOrder() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		xmlList.add("silveira");
		assertEquals("guilherme", strategy.map.get("0"));
		assertEquals("silveira", strategy.map.get("1"));
	}

	public void testWritesASecondObjectInAPositionHigherThanTheListsSize() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		try {
			xmlList.add("silveira");
			xmlList.add(3, "guilherme");
			fail();
		} catch (IndexOutOfBoundsException ex) {
			// ok
		}
	}

	public void testRemovesAWrittenObject() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		boolean changed = xmlList.remove("guilherme");
		assertFalse(strategy.map.containsValue("guilherme"));
	}

	public void testRemovesAWrittenObjectImplyingInAChangeInTheList() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		boolean changed = xmlList.remove("guilherme");
		assertTrue(changed);
	}

	public void testRemovesAnInvalidObjectWithoutAffectingTheList() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		boolean removed = xmlList.remove("guilherme");
		assertFalse(removed);
	}

	public void testHasZeroLengthWhenInstantiated() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		assertEquals(0, xmlList.size());
	}

	public void testHasOneItem() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		assertEquals(1, xmlList.size());
	}

	public void testHasTwoItems() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		xmlList.add("silveira");
		assertEquals(2, xmlList.size());
	}

	public void testIsNotEmpty() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		assertFalse(xmlList.isEmpty());
	}

	public void testDoesNotContainKey() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		assertFalse(xmlList.contains("guilherme"));
	}

	public void testContainsKey() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		assertTrue(xmlList.contains("guilherme"));
	}

	public void testGetsAnObject() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		Object onlyValue = xmlList.iterator().next();
		assertEquals("guilherme", onlyValue);
	}

	public void testGetsTheFirstObject() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		assertEquals("guilherme", xmlList.get(0));
	}

	public void testGetsTheSecondObject() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		xmlList.add("silveira");
		assertEquals("silveira", xmlList.get(1));
	}

	public void testInsertsAnObjectInTheMiddleOfTheList() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		xmlList.add("silveira");
		xmlList.add(1, "de azevedo");
		assertEquals("guilherme", xmlList.get(0));
		assertEquals("de azevedo", xmlList.get(1));
		assertEquals("silveira", xmlList.get(2));
	}

	public void testIteratingGuaranteesItsEnumeration() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		xmlList.add("silveira");
		Iterator it = xmlList.iterator();
		assertEquals("guilherme", it.next());
		assertEquals("silveira", it.next());
	}

	public void testIsEmpty() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		assertTrue(xmlList.isEmpty());
	}

	public void testClearsItsObjects() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		xmlList.add("silveira");
		xmlList.clear();
		assertEquals(0, xmlList.size());
	}

	public void testPutsAllAddsTwoItems() {
		Set original = new HashSet();
		original.add("guilherme");
		original.add("silveira");
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.addAll(original);
		assertEquals(2, xmlList.size());
	}

	public void testContainsASpecificValue() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		assertTrue(xmlList.contains("guilherme"));
	}

	public void testDoesNotContainASpecificValue() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		assertFalse(xmlList.contains("zzzz"));
	}

	public void testEntrySetContainsAllItems() {
		Set original = new HashSet();
		original.add("guilherme");
		original.add("silveira");
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		xmlList.add("silveira");
		assertTrue(xmlList.containsAll(original));
	}

	// actually an acceptance test?
	public void testIteratesOverEntryAndChecksWithAnotherInstance() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		xmlList.add("silveira");
		XmlArrayList built = new XmlArrayList(this.strategy);
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Object entry = iter.next();
			assertTrue(built.contains(entry));
		}
	}

	public void testIteratesOverEntrySetContainingTwoItems() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		xmlList.add("silveira");
		List built = new ArrayList();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Object entry = iter.next();
			built.add(entry);
		}
		assertEquals(xmlList, built);
	}

	public void testRemovesAnItemThroughIteration() {
		XmlArrayList xmlList = new XmlArrayList(this.strategy);
		xmlList.add("guilherme");
		xmlList.add("silveira");
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Object entry = iter.next();
			if (entry.equals("guilherme")) {
				iter.remove();
			}
		}
		assertFalse(xmlList.contains("guilherme"));
	}

	private static class MockedStrategy implements StreamStrategy {

		private Map map = new HashMap();

		public Iterator iterator() {
			return map.entrySet().iterator();
		}

		public int size() {
			return map.size();
		}

		public Object get(Object key) {
			return map.get(key);
		}

		public Object put(Object key, Object value) {
			return map.put(key, value);
		}

		public Object remove(Object key) {
			return map.remove(key);
		}

	}

}
