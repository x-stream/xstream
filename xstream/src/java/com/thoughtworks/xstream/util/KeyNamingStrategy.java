package com.thoughtworks.xstream.util;

import java.io.File;

public class KeyNamingStrategy implements NamingStrategy {

	public boolean isValid(File dir, String name) {
		return name.endsWith(".xml");
	}

	public String extractKey(File file) {
		return file.getName().substring(0, file.getName().length() - 4);
	}

	public String getName(Object key) {
		return key.toString() + ".xml";
	}

}