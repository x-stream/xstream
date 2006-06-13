package com.thoughtworks.xstream.util;

import java.io.File;

public interface NamingStrategy {

	boolean isValid(File dir, String name);

	String extractKey(File file);

	String getName(Object key);

}