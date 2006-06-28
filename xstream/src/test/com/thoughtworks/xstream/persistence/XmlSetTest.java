package com.thoughtworks.xstream.persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class XmlSetTest extends TestCase {
	private MockedStrategy strategy;
	public void setUp() throws Exception {
		super.setUp();
		strategy = new MockedStrategy();
	}

	public void testWritesASingleObject() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		assertTrue(strategy.map.containsValue("guilherme"));
	}

	public void testWritesTwoObjects() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		set.add("silveira");
		assertTrue(strategy.map.containsValue("guilherme"));
		assertTrue(strategy.map.containsValue("silveira"));
	}

	public void testRemovesAWrittenObject() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		assertTrue(strategy.map.containsValue("guilherme"));
		boolean changed = set.remove("guilherme");
		assertTrue(changed);
		assertFalse(strategy.map.containsValue("guilherme"));
	}

	public void testRemovesAnInvalidObject() {
		XmlSet set = new XmlSet(this.strategy);
		boolean removed= set.remove("guilherme");
		assertFalse(removed);
	}

	public void testHasZeroLength() {
		XmlSet set = new XmlSet(this.strategy);
		assertEquals(0, set.size());
	}

	public void testHasOneItem() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		assertEquals(1, set.size());
	}

	public void testHasTwoItems() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		set.add("silveira");
		assertEquals(2, set.size());
	}

	public void testIsNotEmpty() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		assertFalse("set should not be empty", set.isEmpty());
	}

	public void testDoesNotContainKey() {
		XmlSet set = new XmlSet(this.strategy);
		assertFalse(set.contains("guilherme"));
	}

	public void testContainsKey() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		assertTrue(set.contains("guilherme"));
	}

	public void testGetsAnObject() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		Object onlyValue =  set.iterator().next();
		assertEquals("guilherme", onlyValue);
	}

	public void testIsEmpty() {
		XmlSet set = new XmlSet(this.strategy);
		assertTrue("set should be empty", set.isEmpty());
	}

	public void testClearsItsObjects() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		set.add("silveira");
		set.clear();
		assertEquals(0, set.size());
	}

	public void testPutsAllAddsTwoItems() {
		Set original = new HashSet();
		original.add("guilherme");
		original.add("silveira");
		XmlSet set = new XmlSet(this.strategy);
		set.addAll(original);
		assertEquals(2, set.size());
	}

	public void testContainsASpecificValue() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		assertTrue(set.contains("guilherme"));
	}

	public void testDoesNotContainASpecificValue() {
		XmlSet set = new XmlSet(this.strategy);
		assertFalse(set.contains("zzzz"));
	}

	public void testEntrySetContainsAllItems() {
		Set original = new HashSet();
		original.add("guilherme");
		original.add("silveira");
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		set.add("silveira");
		assertTrue(set.containsAll(original));
	}

	// actually an acceptance test?
	public void testIteratesOverEntryAndChecksWithAnotherInstance() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		set.add("silveira");
		XmlSet built = new XmlSet(this.strategy);
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			Object entry = iter.next();
			assertTrue(built.contains(entry));
		}
	}

	public void testIteratesOverEntrySetContainingTwoItems() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		set.add("silveira");
		Set built = new HashSet();
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			Object entry = iter.next();
			built.add(entry);
		}
		assertEquals(set, built);
	}

	public void testRemovesAnItemThroughIteration() {
		XmlSet set = new XmlSet(this.strategy);
		set.add("guilherme");
		set.add("silveira");
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			Object entry = iter.next();
			if (entry.equals("guilherme")) {
				iter.remove();
			}
		}
		assertFalse(set.contains("guilherme"));
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
