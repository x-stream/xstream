package com.thoughtworks.xstream.persistence;

import junit.framework.TestCase;

public class KeyNamingStrategyTest extends TestCase {

	public void testConcatenatesXmlExtensionWhileGettingAFilename() {
		KeyNamingStrategy strategy = new KeyNamingStrategy();
		assertEquals("guilherme.xml", strategy.getName("guilherme"));
	}

	public void testConcatenatesXmlExtensionWhileExtractingAKey() {
		KeyNamingStrategy strategy = new KeyNamingStrategy();
		assertEquals("guilherme", strategy.extractKey("guilherme.xml"));
	}

	public void testEscapesNonAcceptableCharacterWhileExtractingAKey() {
		KeyNamingStrategy strategy = new KeyNamingStrategy();
		assertEquals("../guilherme", strategy
				.extractKey("_2e__2e__2f_guilherme.xml"));
	}

	public void testEscapesNonAcceptableCharacterWhileGettingAFilename() {
		KeyNamingStrategy strategy = new KeyNamingStrategy();
		assertEquals("_2e__2e__2f_guilherme.xml", strategy
				.getName("../guilherme"));
	}

	public void testEscapesUTF8NonAcceptableCharacterWhileGettingAFilename() {
		KeyNamingStrategy strategy = new KeyNamingStrategy();
		assertEquals("_5377_guilherme.xml", strategy.getName("\u5377guilherme"));
	}

	public void testEscapesUTF8NonAcceptableCharacterWhileExtractingAKey() {
		KeyNamingStrategy strategy = new KeyNamingStrategy();
		assertEquals("\u5377guilherme", strategy
				.extractKey("_5377_guilherme.xml"));
	}

	public void testEscapesUnderlineWhileGettingAFilename() {
		KeyNamingStrategy strategy = new KeyNamingStrategy();
		assertEquals("__guilherme.xml", strategy.getName("_guilherme"));
	}

	public void testEscapesUnderlineWhileExtractingAKey() {
		KeyNamingStrategy strategy = new KeyNamingStrategy();
		assertEquals("_guilherme", strategy.extractKey("__guilherme.xml"));
	}

}
