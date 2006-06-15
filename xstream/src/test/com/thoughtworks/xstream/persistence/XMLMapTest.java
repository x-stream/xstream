package com.thoughtworks.xstream.persistence;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.persistence.XMLMap;

import junit.framework.TestCase;

public class XMLMapTest extends TestCase {

	private final File baseDir = new File("tmp-xstream-test");

	protected void setUp() throws Exception {
		super.setUp();
		if (baseDir.exists()) {
			clear(baseDir);
		}
		baseDir.mkdirs();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		clear(baseDir);
	}

	private void clear(File dir) {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				boolean deleted = files[i].delete();
				if (!deleted) {
					throw new RuntimeException(
							"Unable to continue testing: unable to remove file "
									+ files[i].getAbsolutePath());
				}
			}
		}
		dir.delete();
	}

	public void testWritesASingleFile() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		File file = new File(baseDir, "guilherme.xml");
		assertTrue(file.exists());
	}

	public void testWritesTwoFiles() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		assertTrue(new File(baseDir, "guilherme.xml").exists());
		assertTrue(new File(baseDir, "silveira.xml").exists());
	}

	public void testRemovesAWrittenFile() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		assertTrue(new File(baseDir, "guilherme.xml").exists());
		String aCuteString = (String) map.remove("guilherme");
		assertEquals("aCuteString", aCuteString);
		assertFalse(new File(baseDir, "guilherme.xml").exists());
	}

	public void testRemovesAnInvalidFile() {
		XMLMap map = new XMLMap(baseDir);
		String aCuteString = (String) map.remove("guilherme");
		assertNull(aCuteString);
	}

	public void testHasZeroLength() {
		XMLMap map = new XMLMap(baseDir);
		assertEquals(map.size(), 0);
	}

	public void testHasOneItem() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		assertEquals(map.size(), 1);
	}

	public void testHasTwoItems() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		assertEquals(map.size(), 2);
	}

	public void testIsNotEmpty() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		assertFalse("Map should not be empty", map.isEmpty());
	}

	public void testDoesNotContainKey() {
		XMLMap map = new XMLMap(baseDir);
		assertFalse(map.containsKey("guilherme"));
	}

	public void testContainsKey() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		assertTrue(map.containsKey("guilherme"));
	}

	public void testGetsAFile() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		assertTrue(new File(baseDir, "guilherme.xml").exists());
		String aCuteString = (String) map.get("guilherme");
		assertEquals("aCuteString", aCuteString);
	}

	public void testGetsAnInvalidFile() {
		XMLMap map = new XMLMap(baseDir);
		String aCuteString = (String) map.get("guilherme");
		assertNull(aCuteString);
	}

	public void testRewritesASingleFile() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		File file = new File(baseDir, "guilherme.xml");
		assertTrue(file.exists());
		map.put("guilherme", "anotherCuteString");
		assertEquals("anotherCuteString", map.get("guilherme"));
	}

	public void testIsEmpty() {
		XMLMap map = new XMLMap(baseDir);
		assertTrue("Map should be empty", map.isEmpty());
	}

	public void testClearsItsFiles() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		map.clear();
		assertEquals(0, map.size());
	}

	public void testPutsAllAddsTwoItems() {
		Map original = new HashMap();
		original.put("guilherme", "aCuteString");
		original.put("silveira", "anotherCuteString");
		XMLMap map = new XMLMap(baseDir);
		map.putAll(original);
		assertEquals(2, map.size());
	}

	public void testContainsASpecificValue() {
		XMLMap map = new XMLMap(baseDir);
		String value = "aCuteString";
		map.put("guilherme", value);
		assertTrue(map.containsValue(value));
	}

	public void testDoesNotContainASpecificValue() {
		XMLMap map = new XMLMap(baseDir);
		assertFalse(map.containsValue("zzzz"));
	}

	public void testEntrySetContainsAllItems() {
		Map original = new HashMap();
		original.put("guilherme", "aCuteString");
		original.put("silveira", "anotherCuteString");
		Set originalSet = original.entrySet();
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		Set set = map.entrySet();
		assertTrue(set.containsAll(originalSet));
	}

	// actually an acceptance test?
	public void testIteratesOverEntryAndChecksItsKeyWithAnotherInstance() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		XMLMap built = new XMLMap(baseDir);
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			assertTrue(built.containsKey(entry.getKey()));
		}
	}

	// actually an acceptance test?
	public void testIteratesOverEntryAndChecksItsValueWithAnotherInstance() {
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		XMLMap built = new XMLMap(baseDir);
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			assertTrue(built.containsValue(entry.getValue()));
		}
	}

	public void testIteratesOverEntrySetContainingTwoItems() {
		XMLMap map = new XMLMap(baseDir);
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
		XMLMap map = new XMLMap(baseDir);
		map.put("guilherme", "aCuteString");
		map.put("silveira", "anotherCuteString");
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			if(entry.getKey().equals("guilherme")) {
				iter.remove();
			}
		}
		assertFalse(map.containsKey("guilherme"));
	}

}
