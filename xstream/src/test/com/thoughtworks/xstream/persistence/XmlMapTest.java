package com.thoughtworks.xstream.persistence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class XmlMapTest extends TestCase {

	private MockedStrategy strategy;

	public void setUp() throws Exception {
		super.setUp();
		strategy = new MockedStrategy();
	}

	public void testWritesASingleObject() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		assertTrue(strategy.map.containsKey("guilherme"));
	}

	public void testWritesTwoObjects() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		assertTrue(strategy.map.containsKey("guilherme"));
		assertTrue(strategy.map.containsKey("silveira"));
	}

	public void testRemovesAWrittenObject() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		assertTrue(strategy.map.containsKey("guilherme"));
		String aCuteString = (String) map.remove("guilherme");
		assertEquals("aCuteString", aCuteString);
		assertFalse(strategy.map.containsKey("guilherme"));
	}

	public void testRemovesAnInvalidObject() {
		XmlMap map = new XmlMap(this.strategy);
		String aCuteString = (String) map.remove("guilherme");
		assertNull(aCuteString);
	}

	public void testHasZeroLength() {
		XmlMap map = new XmlMap(this.strategy);
		assertEquals(0, map.size());
	}

	public void testHasOneItem() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		assertEquals(1, map.size());
	}

	public void testHasTwoItems() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		assertEquals(2, map.size());
	}

	public void testIsNotEmpty() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		assertFalse("Map should not be empty", map.isEmpty());
	}

	public void testDoesNotContainKey() {
		XmlMap map = new XmlMap(this.strategy);
		assertFalse(map.containsKey("guilherme"));
	}

	public void testContainsKey() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		assertTrue(map.containsKey("guilherme"));
	}

	public void testGetsAnObject() {
		XmlMap map = new XmlMap(this.strategy);
		this.strategy.map.put("guilherme", "aCuteString");
		String aCuteString = (String) map.get("guilherme");
		assertEquals("aCuteString", aCuteString);
	}

	public void testGetsAnInvalidObject() {
		XmlMap map = new XmlMap(this.strategy);
		String aCuteString = (String) map.get("guilherme");
		assertNull(aCuteString);
	}

	public void testRewritesASingleObject() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		assertEquals("aCuteString", map.get("guilherme"));
		map.put("guilherme", "anotherCuteString");
		assertEquals("anotherCuteString", map.get("guilherme"));
	}

	public void testIsEmpty() {
		XmlMap map = new XmlMap(this.strategy);
		assertTrue("Map should be empty", map.isEmpty());
	}

	public void testClearsItsObjects() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		map.clear();
		assertEquals(0, map.size());
	}

	public void testPutsAllAddsTwoItems() {
		Map original = new HashMap();
		original.put("guilherme", "aCuteString");
		original.put("silveira", "anotherCuteString");
		XmlMap map = new XmlMap(this.strategy);
		map.putAll(original);
		assertEquals(2, map.size());
	}

	public void testContainsASpecificValue() {
		XmlMap map = new XmlMap(this.strategy);
		String value = "aCuteString";
		map.put("guilherme", value);
		assertTrue(map.containsValue(value));
	}

	public void testDoesNotContainASpecificValue() {
		XmlMap map = new XmlMap(this.strategy);
		assertFalse(map.containsValue("zzzz"));
	}

	public void testEntrySetContainsAllItems() {
		Map original = new HashMap();
		original.put("guilherme", "aCuteString");
		original.put("silveira", "anotherCuteString");
		Set originalSet = original.entrySet();
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		Set set = map.entrySet();
		assertTrue(set.containsAll(originalSet));
	}

	// actually an acceptance test?
	public void testIteratesOverEntryAndChecksItsKeyWithAnotherInstance() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		XmlMap built = new XmlMap(this.strategy);
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			assertTrue(built.containsKey(entry.getKey()));
		}
	}

	// actually an acceptance test?
	public void testIteratesOverEntryAndChecksItsValueWithAnotherInstance() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		XmlMap built = new XmlMap(this.strategy);
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			assertTrue(built.containsValue(entry.getValue()));
		}
	}

	public void testIteratesOverEntrySetContainingTwoItems() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		Map built = new HashMap();
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			built.put(entry.getKey(), entry.getValue());
		}
		assertEquals(map, built);
	}

	public void testRemovesAnItemThroughIteration() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			if (entry.getKey().equals("guilherme")) {
				iter.remove();
			}
		}
		assertFalse(map.containsKey("guilherme"));
	}

	public void testRewritesAObject() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		map.put("guilherme", "anotherCuteString");
		assertEquals("anotherCuteString", map.get("guilherme"));
	}

	public void testPutReturnsTheOldValueWhenRewritingAObject() {
		XmlMap map = new XmlMap(this.strategy);
		map.put("guilherme", "aCuteString");
		assertEquals("aCuteString", map.put("guilherme", "anotherCuteString"));
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
