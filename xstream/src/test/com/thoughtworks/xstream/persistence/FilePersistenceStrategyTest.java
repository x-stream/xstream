/*
 * Copyright (C) 2008, 2009, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 21. November 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.persistence;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 */
public class FilePersistenceStrategyTest extends TestCase {

    private final File baseDir = new File("target/tmp");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (baseDir.exists()) {
            clear(baseDir);
        }
        baseDir.mkdirs();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        clear(baseDir);
    }

    private void clear(final File dir) {
        final File[] files = dir.listFiles();
        for (final File file : files) {
            if (file.isFile()) {
                final boolean deleted = file.delete();
                if (!deleted) {
                    throw new RuntimeException("Unable to continue testing: unable to remove file "
                        + file.getAbsolutePath());
                }
            }
        }
        dir.delete();
    }

    public void testConcatenatesXmlExtensionWhileGettingAFilename() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals("string@guilherme.xml", strategy.getName("guilherme"));
    }

    public void testConcatenatesXmlExtensionWhileExtractingAKey() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals("guilherme", strategy.extractKey("string@guilherme.xml"));
    }

    public void testEscapesNonAcceptableCharacterWhileExtractingAKey() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals("../guilherme", strategy.extractKey("string@..%2Fguilherme.xml"));
    }

    public void testEscapesNonAcceptableCharacterWhileGettingAFilename() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals("string@..%2Fguilherme.xml", strategy.getName("../guilherme"));
    }

    public void testEscapesUTF8NonAcceptableCharacterWhileGettingAFilename() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals("string@\u5377guilherme.xml", strategy.getName("\u5377guilherme"));
    }

    public void testEscapesUTF8NonAcceptableCharacterWhileExtractingAKey() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals("\u5377guilherme", strategy.extractKey("string@\u5377guilherme.xml"));
    }

    public void testEscapesPercentageWhileGettingAFilename() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals("string@%25guilherme.xml", strategy.getName("%guilherme"));
    }

    public void testEscapesPercentageWhileExtractingAKey() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals("%guilherme", strategy.extractKey("string@%25guilherme.xml"));
    }

    public void testEscapesNullKeyWhileGettingAFileName() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals("null@null.xml", strategy.getName(null));
    }

    public void testRestoresTypeOfKey() throws MalformedURLException {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals(new URL("http://xstream.codehaus.org"), strategy.extractKey(
            "url@http%3A%2F%2Fxstream.codehaus.org.xml"));
    }

    public void testEscapesNullKeyWhileExtractingKey() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertNull(strategy.extractKey("null@null.xml"));
    }

    public void testWritesASingleFile() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        final File file = new File(baseDir, "string@guilherme.xml");
        assertTrue(file.isFile());
    }

    public void testWritesTwoFiles() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        assertTrue(new File(baseDir, "string@guilherme.xml").isFile());
        assertTrue(new File(baseDir, "string@silveira.xml").isFile());
    }

    public void testRemovesAWrittenFile() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertTrue(new File(baseDir, "string@guilherme.xml").isFile());
        final String aCuteString = strategy.remove("guilherme");
        assertEquals("aCuteString", aCuteString);
        assertFalse(new File(baseDir, "string@guilherme.xml").exists());
    }

    public void testRemovesAnInvalidFile() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        final String aCuteString = strategy.remove("guilherme");
        assertNull(aCuteString);
    }

    public void testHasZeroLength() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals(0, strategy.size());
    }

    public void testHasOneItem() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertEquals(1, strategy.size());
    }

    public void testHasTwoItems() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        assertEquals(2, strategy.size());
    }

    public void testIsNotEmpty() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertEquals("Map should not be empty", 1, strategy.size());
    }

    public void testDoesNotContainKey() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertFalse(strategy.containsKey("guilherme"));
    }

    public void testContainsKey() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertTrue(strategy.containsKey("guilherme"));
    }

    public void testGetsAFile() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertTrue(new File(baseDir, "string@guilherme.xml").isFile());
        final String aCuteString = strategy.get("guilherme");
        assertEquals("aCuteString", aCuteString);
    }

    public void testGetsAnInvalidFile() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        final String aCuteString = strategy.get("guilherme");
        assertNull(aCuteString);
    }

    public void testRewritesASingleFile() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        final File file = new File(baseDir, "string@guilherme.xml");
        assertTrue(file.isFile());
        strategy.put("guilherme", "anotherCuteString");
        assertEquals("anotherCuteString", strategy.get("guilherme"));
    }

    public void testIsEmpty() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        assertEquals("Map should be empty", 0, strategy.size());
    }

    public void testContainsAllItems() {
        final Map<String, String> original = new HashMap<>();
        original.put("guilherme", "aCuteString");
        original.put("silveira", "anotherCuteString");
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
	original.keySet().forEach((key) -> {
	    assertTrue(strategy.containsKey(key));
	});
    }

    public void testIteratesOverEntryAndChecksItsKeyWithAnotherInstance() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        final FilePersistenceStrategy<String, String> built = new FilePersistenceStrategy<>(baseDir);
        for (final Iterator<Map.Entry<String, String>> iter = strategy.iterator(); iter.hasNext();) {
            final Map.Entry<String, String> entry = iter.next();
            assertTrue(built.containsKey(entry.getKey()));
        }
    }

    public void testRemovesAnItemThroughIteration() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        for (final Iterator<Map.Entry<String, String>> iter = strategy.iterator(); iter.hasNext();) {
            final Map.Entry<String, String> entry = iter.next();
            if (entry.getKey().equals("guilherme")) {
                iter.remove();
            }
        }
        assertFalse(strategy.containsKey("guilherme"));
    }

    public void testRewritesAFile() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("guilherme", "anotherCuteString");
        assertEquals("anotherCuteString", strategy.get("guilherme"));
    }

    public void testPutReturnsTheOldValueWhenRewritingAFile() {
        final FilePersistenceStrategy<String, String> strategy = new FilePersistenceStrategy<>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertEquals("aCuteString", strategy.put("guilherme", "anotherCuteString"));
    }

}
