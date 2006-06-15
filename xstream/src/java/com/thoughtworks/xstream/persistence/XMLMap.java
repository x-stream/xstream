package com.thoughtworks.xstream.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

/**
 * A persistent map. Its values are actually serialized as xml files. If you
 * need an application-wide synchronized version of this map, try the respective
 * Collections methods.
 * 
 * @author Guilherme Silveira
 */
// TODO implement a cached version
// TODO overwrite the keySet method for better performance
public class XMLMap extends AbstractMap {

	private final File baseDirectory;

	private final NamingStrategy namingStrategy;

	private final XStream xstream;

	private final FilenameFilter filter;

	public XMLMap(File baseDirectory) {
		this(baseDirectory, new XStream());
	}

	public XMLMap(File baseDirectory, XStream xstream) {
		this(baseDirectory, xstream, new KeyNamingStrategy());
	}

	public XMLMap(File baseDirectory, XStream xstream,
			final NamingStrategy namingStrategy) {
		this.baseDirectory = baseDirectory;
		this.xstream = xstream;
		this.namingStrategy = namingStrategy;
		this.filter = new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return new File(dir, name).isFile()
						&& namingStrategy.isValid(dir, name);
			}

		};
	}

	public int size() {
		return baseDirectory.list(filter).length;
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

	private File getFile(String filename) {
		return new File(this.baseDirectory, filename);
	}

	public boolean containsKey(Object key) {
		// faster lookup
		File file = getFile(this.namingStrategy.getName(key));
		return file.exists();
	}

	public Object get(Object key) {
		// faster lookup
		return readFile(getFile(this.namingStrategy.getName(key)));
	}

	public Object put(Object key, Object value) {
		String filename = namingStrategy.getName(key);
		writeFile(new File(baseDirectory, filename), value);
		return value;
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

	public Object remove(Object key) {
		// faster lookup
		File file = getFile(this.namingStrategy.getName(key));
		Object value = null;
		if (file.exists()) {
			value = readFile(file);
			file.delete();
		}
		return value;
	}

	public void putAll(Map t) {
		for (Iterator it = t.keySet().iterator(); it.hasNext();) {
			Object key = (Object) it.next();
			put(key, t.get(key));
		}
	}

	public Set entrySet() {
		return new XMLMapEntries();
	}

	class XMLMapEntries extends AbstractSet {

		public int size() {
			return XMLMap.this.size();
		}

		public boolean isEmpty() {
			return XMLMap.this.isEmpty();
		}

		public Iterator iterator() {
			return new XMLMapEntriesIterator();
		}

	}

	class XMLMapEntriesIterator implements Iterator {

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

				private String key = namingStrategy.extractKey(file);

				public Object getKey() {
					return key;
				}

				public Object getValue() {
					return readFile(file);
				}

				public Object setValue(Object value) {
					return XMLMap.this.put(key, value);
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

}