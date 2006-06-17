package com.thoughtworks.xstream.persistence;

import java.io.File;

/**
 * A key to filename and vice-versa strategy interface.
 * 
 * @author Guilherme Silveira
 */
public interface NamingStrategy {

	boolean isValid(File dir, String name);

	String extractKey(String name);

	String getName(Object key);

}