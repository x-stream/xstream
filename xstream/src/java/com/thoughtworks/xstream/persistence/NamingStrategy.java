package com.thoughtworks.xstream.persistence;

import java.io.File;

public interface NamingStrategy {

	boolean isValid(File dir, String name);

	String extractKey(File file);

	String getName(Object key);

}