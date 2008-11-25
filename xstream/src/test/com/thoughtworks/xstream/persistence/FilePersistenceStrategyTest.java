/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 21. November 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.persistence;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 */
public class FilePersistenceStrategyTest extends TestCase {

    private final File baseDir = new File("target/tmp");

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
        for (int i = 0; i < files.length; i++ ) {
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
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals("string@guilherme.xml", strategy.getName("guilherme"));
    }

    public void testConcatenatesXmlExtensionWhileExtractingAKey() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals("guilherme", strategy.extractKey("string@guilherme.xml"));
    }

    public void testEscapesNonAcceptableCharacterWhileExtractingAKey() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals("../guilherme", strategy.extractKey("string@..%2Fguilherme.xml"));
    }

    public void testEscapesNonAcceptableCharacterWhileGettingAFilename() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals("string@..%2Fguilherme.xml", strategy.getName("../guilherme"));
    }

    public void testEscapesUTF8NonAcceptableCharacterWhileGettingAFilename() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals("string@\u5377guilherme.xml", strategy.getName("\u5377guilherme"));
    }

    public void testEscapesUTF8NonAcceptableCharacterWhileExtractingAKey() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals("\u5377guilherme", strategy.extractKey("string@\u5377guilherme.xml"));
    }

    public void testEscapesPercentageWhileGettingAFilename() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals("string@%25guilherme.xml", strategy.getName("%guilherme"));
    }

    public void testEscapesPercentageWhileExtractingAKey() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals("%guilherme", strategy.extractKey("string@%25guilherme.xml"));
    }

    public void testEscapesNullKeyWhileGettingAFileName() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals("null@null.xml", strategy.getName(null));
    }

    public void testRestoresTypeOfKey() throws MalformedURLException {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals(new URL("http://xstream.codehaus.org"), strategy
            .extractKey("url@http%3A%2F%2Fxstream.codehaus.org.xml"));
    }

    public void testEscapesNullKeyWhileExtractingKey() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertNull(strategy.extractKey("null@null.xml"));
    }

    public void testWritesASingleFile() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        File file = new File(baseDir, "string@guilherme.xml");
        assertTrue(file.isFile());
    }

    public void testWritesTwoFiles() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        assertTrue(new File(baseDir, "string@guilherme.xml").isFile());
        assertTrue(new File(baseDir, "string@silveira.xml").isFile());
    }

    public void testRemovesAWrittenFile() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertTrue(new File(baseDir, "string@guilherme.xml").isFile());
        String aCuteString = (String)strategy.remove("guilherme");
        assertEquals("aCuteString", aCuteString);
        assertFalse(new File(baseDir, "string@guilherme.xml").exists());
    }

    public void testRemovesAnInvalidFile() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        String aCuteString = (String)strategy.remove("guilherme");
        assertNull(aCuteString);
    }

    public void testHasZeroLength() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals(0, strategy.size());
    }

    public void testHasOneItem() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertEquals(1, strategy.size());
    }

    public void testHasTwoItems() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        assertEquals(2, strategy.size());
    }

    public void testIsNotEmpty() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertEquals("Map should not be empty", 1, strategy.size());
    }

    public void testDoesNotContainKey() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertFalse(strategy.containsKey("guilherme"));
    }

    public void testContainsKey() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertTrue(strategy.containsKey("guilherme"));
    }

    public void testGetsAFile() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertTrue(new File(baseDir, "string@guilherme.xml").isFile());
        String aCuteString = (String)strategy.get("guilherme");
        assertEquals("aCuteString", aCuteString);
    }

    public void testGetsAnInvalidFile() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        String aCuteString = (String)strategy.get("guilherme");
        assertNull(aCuteString);
    }

    public void testRewritesASingleFile() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        File file = new File(baseDir, "string@guilherme.xml");
        assertTrue(file.isFile());
        strategy.put("guilherme", "anotherCuteString");
        assertEquals("anotherCuteString", strategy.get("guilherme"));
    }

    public void testIsEmpty() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        assertEquals("Map should be empty", 0, strategy.size());
    }

    public void testEntrySetContainsAllItems() {
        Map original = new HashMap();
        original.put("guilherme", "aCuteString");
        original.put("silveira", "anotherCuteString");
        Set originalSet = original.entrySet();
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        for (Iterator iter = original.keySet().iterator(); iter.hasNext();) {
            assertTrue(strategy.containsKey(iter.next()));
        }
    }

    // actually an acceptance test?
    public void testIteratesOverEntryAndChecksItsKeyWithAnotherInstance() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        FilePersistenceStrategy built = new FilePersistenceStrategy(baseDir);
        for (Iterator iter = strategy.iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            assertTrue(built.containsKey(entry.getKey()));
        }
    }

    public void testRemovesAnItemThroughIteration() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("silveira", "anotherCuteString");
        for (Iterator iter = strategy.iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            if (entry.getKey().equals("guilherme")) {
                iter.remove();
            }
        }
        assertFalse(strategy.containsKey("guilherme"));
    }

    public void testRewritesAFile() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        strategy.put("guilherme", "anotherCuteString");
        assertEquals("anotherCuteString", strategy.get("guilherme"));
    }

    public void testPutReturnsTheOldValueWhenRewritingAFile() {
        FilePersistenceStrategy strategy = new FilePersistenceStrategy(baseDir);
        strategy.put("guilherme", "aCuteString");
        assertEquals("aCuteString", strategy.put("guilherme", "anotherCuteString"));
    }

}
