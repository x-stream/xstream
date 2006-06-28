package com.thoughtworks.xstream.persistence;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class FileStreamStrategyTest extends TestCase {

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

	public void testConcatenatesXmlExtensionWhileGettingAFilename() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		assertEquals("guilherme.xml", strategy.getName("guilherme"));
	}

	public void testConcatenatesXmlExtensionWhileExtractingAKey() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		assertEquals("guilherme", strategy.extractKey("guilherme.xml"));
	}

	public void testEscapesNonAcceptableCharacterWhileExtractingAKey() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		assertEquals("../guilherme", strategy
				.extractKey("_2e__2e__2f_guilherme.xml"));
	}

	public void testEscapesNonAcceptableCharacterWhileGettingAFilename() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		assertEquals("_2e__2e__2f_guilherme.xml", strategy
				.getName("../guilherme"));
	}

	public void testEscapesUTF8NonAcceptableCharacterWhileGettingAFilename() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		assertEquals("_5377_guilherme.xml", strategy.getName("\u5377guilherme"));
	}

	public void testEscapesUTF8NonAcceptableCharacterWhileExtractingAKey() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		assertEquals("\u5377guilherme", strategy
				.extractKey("_5377_guilherme.xml"));
	}

	public void testEscapesUnderlineWhileGettingAFilename() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		assertEquals("__guilherme.xml", strategy.getName("_guilherme"));
	}

	public void testEscapesUnderlineWhileExtractingAKey() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		assertEquals("_guilherme", strategy.extractKey("__guilherme.xml"));
	}

	public void testWritesASingleFile() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		File file = new File(baseDir, "guilherme.xml");
		assertTrue(file.exists());
	}

	public void testWritesTwoFiles() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		strategy.put("silveira", "anotherCuteString");
		assertTrue(new File(baseDir, "guilherme.xml").exists());
		assertTrue(new File(baseDir, "silveira.xml").exists());
	}

	public void testRemovesAWrittenFile() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		assertTrue(new File(baseDir, "guilherme.xml").exists());
		String aCuteString = (String) strategy.remove("guilherme");
		assertEquals("aCuteString", aCuteString);
		assertFalse(new File(baseDir, "guilherme.xml").exists());
	}

	public void testRemovesAnInvalidFile() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		String aCuteString = (String) strategy.remove("guilherme");
		assertNull(aCuteString);
	}

	public void testHasZeroLength() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		assertEquals(0, strategy.size());
	}

	public void testHasOneItem() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		assertEquals(1, strategy.size());
	}

	public void testHasTwoItems() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		strategy.put("silveira", "anotherCuteString");
		assertEquals(2, strategy.size());
	}

	public void testIsNotEmpty() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		assertEquals("Map should not be empty", 1,strategy.size());
	}

	public void testDoesNotContainKey() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		assertFalse(strategy.containsKey("guilherme"));
	}

	public void testContainsKey() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		assertTrue(strategy.containsKey("guilherme"));
	}

	public void testGetsAFile() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		assertTrue(new File(baseDir, "guilherme.xml").exists());
		String aCuteString = (String) strategy.get("guilherme");
		assertEquals("aCuteString", aCuteString);
	}

	public void testGetsAnInvalidFile() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		String aCuteString = (String) strategy.get("guilherme");
		assertNull(aCuteString);
	}

	public void testRewritesASingleFile() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		File file = new File(baseDir, "guilherme.xml");
		assertTrue(file.exists());
		strategy.put("guilherme", "anotherCuteString");
		assertEquals("anotherCuteString", strategy.get("guilherme"));
	}

	public void testIsEmpty() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		assertEquals("Map should be empty", 0, strategy.size());
	}
	
	public void testEntrySetContainsAllItems() {
		Map original = new HashMap();
		original.put("guilherme", "aCuteString");
		original.put("silveira", "anotherCuteString");
		Set originalSet = original.entrySet();
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		strategy.put("silveira", "anotherCuteString");
		for (Iterator iter = original.keySet().iterator(); iter.hasNext();) {
			assertTrue(strategy.containsKey(iter.next()));
		}
	}

	// actually an acceptance test?
	public void testIteratesOverEntryAndChecksItsKeyWithAnotherInstance() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		strategy.put("silveira", "anotherCuteString");
		FileStreamStrategy built = new FileStreamStrategy(baseDir);
		for (Iterator iter = strategy.iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			assertTrue(built.containsKey(entry.getKey()));
		}
	}

	public void testRemovesAnItemThroughIteration() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		strategy.put("silveira", "anotherCuteString");
		for (Iterator iter = strategy.iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			if (entry.getKey().equals("guilherme")) {
				iter.remove();
			}
		}
		assertFalse(strategy.containsKey("guilherme"));
	}

	public void testRewritesAFile() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		strategy.put("guilherme", "anotherCuteString");
		assertEquals("anotherCuteString", strategy.get("guilherme"));
	}

	public void testPutReturnsTheOldValueWhenRewritingAFile() {
		FileStreamStrategy strategy = new FileStreamStrategy(baseDir);
		strategy.put("guilherme", "aCuteString");
		assertEquals("aCuteString", strategy.put("guilherme", "anotherCuteString"));
	}



}
