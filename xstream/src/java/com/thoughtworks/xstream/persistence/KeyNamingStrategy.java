package com.thoughtworks.xstream.persistence;

import java.io.File;

/**
 * The default naming strategy is based on the key's toString method and escapes
 * non digit, non a-z, A-Z characters. In order to change the
 * escaping/unescaping algorithm, simply extend this class and rewrite its
 * getName/extractKey methods.
 * 
 * @author Guilherme Silveira
 */
// TODO cache keys?
public class KeyNamingStrategy implements NamingStrategy {

	public boolean isValid(File dir, String name) {
		return name.endsWith(".xml");
	}

	/**
	 * Given a filename, the unescape method returns the key which originated
	 * it.
	 * 
	 * @param name
	 *            the filename
	 * @return the original key
	 */
	public String extractKey(String name) {
		return unescape(name.substring(0, name.length() - 4));
	}

	protected String unescape(String name) {
		StringBuffer buffer = new StringBuffer();
		int currentValue = -1;
		// do we have a regex master to do it?
		char[] array = name.toCharArray();
		for (int i = 0; i < array.length; i++) {
			char c = array[i];
			if (c == '_' && currentValue != -1) {
				if (currentValue == 0) {
					buffer.append('_');
				} else {
					buffer.append((char) currentValue);
				}
				currentValue = -1;
			} else if (c == '_') {
				currentValue = 0;
			} else if (currentValue != -1) {
				currentValue = currentValue * 16
						+ Integer.parseInt(Character.toString(c), 16);
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	/**
	 * Given a key, the escape method returns the filename which shall be used.
	 * 
	 * @param key
	 *            the key
	 * @return the desired and escaped filename
	 */
	public String getName(Object key) {
		return escape(key.toString()) + ".xml";
	}

	private String escape(String key) {
		// do we have a regex master to do it?
		StringBuffer buffer = new StringBuffer();
		char[] array = key.toCharArray();
		for (int i = 0; i < array.length; i++) {
			char c = array[i];
			if (Character.isDigit(c) || (c >= 'A' && c <= 'Z')
					|| (c >= 'a' && c <= 'z')) {
				buffer.append(c);
			} else if (c == '_') {
				buffer.append("__");
			} else {
				buffer.append("_" + (Integer.toHexString(c)) + "_");
			}
		}
		return buffer.toString();
	}

}