/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. June 2006 by Guilherme Silveira
 */
package com.thoughtworks.xstream.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

/**
 * The default naming strategy is based on the key's toString method and escapes
 * non digit, non a-z, A-Z characters. In order to change the
 * escaping/unescaping algorithm, simply extend this class and rewrite its
 * getName/extractKey methods.
 * 
 * @author Guilherme Silveira
 */
// TODO cache keys?
public class FileStreamStrategy implements StreamStrategy {

	private final FilenameFilter filter;

	private final XStream xstream;

	private final File baseDirectory;

	public FileStreamStrategy(File baseDirectory) {
		this(baseDirectory, new XStream());
	}

	public FileStreamStrategy(File baseDirectory, XStream xstream) {
		this.baseDirectory = baseDirectory;
		this.xstream = xstream;
		this.filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return new File(dir, name).isFile() && isValid(dir, name);
			}
		};
	}

	protected boolean isValid(File dir, String name) {
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
	protected String extractKey(String name) {
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
						+ Integer.parseInt(String.valueOf(c), 16);
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
	protected String getName(Object key) {
		return escape(key.toString()) + ".xml";
	}

	protected String escape(String key) {
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
	
	class XmlMapEntriesIterator implements Iterator {

		private File[] files = baseDirectory.listFiles(filter);

		private int position = -1;

		private File current = null;

		public boolean hasNext() {
			return position + 1 < files.length;
		}

		public void remove() {
			if (current == null) {
				throw new IllegalStateException();
			}
			// removes without loading
			current.delete();
		}

		public Object next() {
			return new Map.Entry() {

				private File file = current = files[++position];

				private String key = extractKey(file.getName());

				public Object getKey() {
					return key;
				}

				public Object getValue() {
					return readFile(file);
				}

				public Object setValue(Object value) {
					return put(key, value);
				}

				public boolean equals(Object obj) {
					if (!(obj instanceof Entry)) {
						return false;
					}
					Entry e2 = (Entry) obj;
					// TODO local cache value instead of calling getValue twice
					return (key == null ? e2.getKey() == null : key.equals(e2
							.getKey()))
							&& (getValue() == null ? e2.getValue() == null
									: getValue().equals(e2.getValue()));
				}

			};
		}

	}

	private void writeFile(File file, Object value) {
		try {
			OutputStream os = new FileOutputStream(file);
			try {
				this.xstream.toXML(value, os);
			} finally {
				os.close();
			}
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	private File getFile(String filename) {
		return new File(this.baseDirectory, filename);
	}

	private Object readFile(File file) {
		try {
			InputStream is = new FileInputStream(file);
			try {
				return this.xstream.fromXML(is);
			} finally {
				is.close();
			}
		} catch (FileNotFoundException e) {
			// not found... file.exists might generate a sync problem
			return null;
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}


	public Object put(Object key, Object value) {
		Object oldValue = get(key);
		String filename = getName(key);
		writeFile(new File(baseDirectory, filename), value);
		return oldValue;
	}
	
	public Iterator iterator() {
		return new XmlMapEntriesIterator();
	}

	public int size() {
		return baseDirectory.list(filter).length;
	}

	public boolean containsKey(Object key) {
		// faster lookup
		File file = getFile(getName(key));
		return file.exists();
	}

	public Object get(Object key) {
		return readFile(getFile(getName(key)));
	}

	public Object remove(Object key) {
		// faster lookup
		File file = getFile(getName(key));
		Object value = null;
		if (file.exists()) {
			value = readFile(file);
			file.delete();
		}
		return value;
	}



}
