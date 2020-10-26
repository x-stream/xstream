/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2007, 2008, 2009, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17. June 2006 by Guilherme Silveira
 */
package com.thoughtworks.xstream.persistence;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;


/**
 * @author Guilherme Silveira
 */
public class FileStreamStrategyTest extends TestCase {

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
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertEquals("guilherme.xml", strategy.getName("guilherme"));
    }

    public void testConcatenatesXmlExtensionWhileExtractingAKey() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertEquals("guilherme", strategy.extractKey("guilherme.xml"));
    }

    public void testEscapesNonAcceptableCharacterWhileExtractingAKey() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertEquals("../guilherme", strategy.extractKey("_2e__2e__2f_guilherme.xml"));
    }

    public void testEscapesNonAcceptableCharacterWhileGettingAFilename() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertEquals("_2e__2e__2f_guilherme.xml", strategy.getName("../guilherme"));
    }

    public void testEscapesUTF8NonAcceptableCharacterWhileGettingAFilename() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertEquals("_5377_guilherme.xml", strategy.getName("\u5377guilherme"));
    }

    public void testEscapesUTF8NonAcceptableCharacterWhileExtractingAKey() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertEquals("\u5377guilherme", strategy.extractKey("_5377_guilherme.xml"));
    }

    public void testEscapesUnderlineWhileGettingAFilename() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertEquals("__guilherme.xml", strategy.getName("_guilherme"));
    }

    public void testEscapesUnderlineWhileExtractingAKey() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertEquals("_guilherme", strategy.extractKey("__guilherme.xml"));
    }

    public void testEscapesNullKeyWhileGettingAFileName() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertEquals("_0_.xml", strategy.getName(null));
    }

    public void testEscapesNullKeyWhileExtractingKey() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertNull(strategy.extractKey("_0_.xml"));
    }

    public void testWritesASingleFile() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        final File file = new File(baseDir, "guilherme.xml");
        assertTrue(file.exists());
    }

    public void testWritesTwoFiles() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        assertTrue(new File(baseDir, "guilherme.xml").exists());
        assertTrue(new File(baseDir, "silveira.xml").exists());
    }

    public void testRemovesAWrittenFile() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertTrue(new File(baseDir, "guilherme.xml").exists());
        final String aCuteString = strategy.remove("guilherme");
        assertEquals("aCuteString", aCuteString);
        assertFalse(new File(baseDir, "guilherme.xml").exists());
    }

    public void testRemovesAnInvalidFile() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        final String aCuteString = strategy.remove("guilherme");
        assertNull(aCuteString);
    }

    public void testHasZeroLength() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertEquals(0, strategy.size());
    }

    public void testHasOneItem() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertEquals(1, strategy.size());
    }

    public void testHasTwoItems() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        assertEquals(2, strategy.size());
    }

    public void testIsNotEmpty() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertEquals("Map should not be empty", 1, strategy.size());
    }

    public void testDoesNotContainKey() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertFalse(strategy.containsKey("guilherme"));
    }

    public void testContainsKey() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertTrue(strategy.containsKey("guilherme"));
    }

    public void testGetsAFile() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertTrue(new File(baseDir, "guilherme.xml").exists());
        final String aCuteString = strategy.get("guilherme");
        assertEquals("aCuteString", aCuteString);
    }

    public void testGetsAnInvalidFile() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        final String aCuteString = strategy.get("guilherme");
        assertNull(aCuteString);
    }

    public void testRewritesASingleFile() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        final File file = new File(baseDir, "guilherme.xml");
        assertTrue(file.exists());
        strategy.put("guilherme", "anotherCuteString");
        assertEquals("anotherCuteString", strategy.get("guilherme"));
    }

    public void testIsEmpty() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        assertEquals("Map should be empty", 0, strategy.size());
    }

    public void testContainsAllItems() {
        final Map<String, String> original = new HashMap<String, String>();
        original.put("guilherme", "aCuteString");
        original.put("silveira", "anotherCuteString");
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        for (final String key : original.keySet()) {
            assertTrue(strategy.containsKey(key));
        }
    }

    // actually an acceptance test?
    public void testIteratesOverEntryAndChecksItsKeyWithAnotherInstance() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        final FileStreamStrategy<String> built = new FileStreamStrategy<String>(baseDir);
        for (final Iterator<Map.Entry<String, String>> iter = strategy.iterator(); iter.hasNext();) {
            final Map.Entry<String, String> entry = iter.next();
            assertTrue(built.containsKey(entry.getKey()));
        }
    }

    public void testRemovesAnItemThroughIteration() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
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
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("guilherme", "anotherCuteString");
        assertEquals("anotherCuteString", strategy.get("guilherme"));
    }

    public void testPutReturnsTheOldValueWhenRewritingAFile() {
        final FileStreamStrategy<String> strategy = new FileStreamStrategy<String>(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertEquals("aCuteString", strategy.put("guilherme", "anotherCuteString"));
    }

}
