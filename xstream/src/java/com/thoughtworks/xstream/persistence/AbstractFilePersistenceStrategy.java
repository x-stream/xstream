/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 18. November 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.persistence;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.mapper.Mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Abstract base class for file based persistence strategies.
 * 
 * @author Guilherme Silveira
 * @author Joerg Schaible
 * @since 1.3.1
 */
public abstract class AbstractFilePersistenceStrategy implements PersistenceStrategy {

    private final FilenameFilter filter;
    private final File baseDirectory;
    private final String encoding;
    private final transient XStream xstream;

    public AbstractFilePersistenceStrategy(
        final File baseDirectory, final XStream xstream, final String encoding) {
        this.baseDirectory = baseDirectory;
        this.xstream = xstream;
        this.encoding = encoding;
        filter = new ValidFilenameFilter();
    }

    protected ConverterLookup getConverterLookup() {
        return xstream.getConverterLookup();
    }

    protected Mapper getMapper() {
        return xstream.getMapper();
    }

    protected boolean isValid(final File dir, final String name) {
        return name.endsWith(".xml");
    }

    /**
     * Given a filename, the unescape method returns the key which originated it.
     * 
     * @param name the filename
     * @return the original key
     */
    protected abstract Object extractKey(String name);

    /**
     * Given a key, the escape method returns the filename which shall be used.
     * 
     * @param key the key
     * @return the desired and escaped filename
     */
    protected abstract String getName(Object key);

    protected class ValidFilenameFilter implements FilenameFilter {
        public boolean accept(final File dir, final String name) {
            return new File(dir, name).isFile() && isValid(dir, name);
        }
    }

    protected class XmlMapEntriesIterator implements Iterator {

        private final File[] files = baseDirectory.listFiles(filter);

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
                private final File file = current = files[ ++position];
                private final Object key = extractKey(file.getName());

                public Object getKey() {
                    return key;
                }

                public Object getValue() {
                    return readFile(file);
                }

                public Object setValue(final Object value) {
                    return put(key, value);
                }

                public boolean equals(final Object obj) {
                    if (!(obj instanceof Entry)) {
                        return false;
                    }
                    Object value = getValue();
                    final Entry e2 = (Entry)obj;
                    Object key2 = e2.getKey();
                    Object value2 = e2.getValue();
                    return (key == null ? key2 == null : key.equals(key2))
                        && (value == null ? value2 == null : getValue().equals(e2.getValue()));
                }
            };
        }
    }

    private void writeFile(final File file, final Object value) {
        try {
            final FileOutputStream out = new FileOutputStream(file);
            final Writer writer = encoding != null
                ? new OutputStreamWriter(out, encoding)
                : new OutputStreamWriter(out);
            try {
                xstream.toXML(value, writer);
            } finally {
                writer.close();
            }
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    private File getFile(final String filename) {
        return new File(baseDirectory, filename);
    }

    private Object readFile(final File file) {
        try {
            final FileInputStream in = new FileInputStream(file);
            final Reader reader = encoding != null
                ? new InputStreamReader(in, encoding)
                : new InputStreamReader(in);
            try {
                return xstream.fromXML(reader);
            } finally {
                reader.close();
            }
        } catch (final FileNotFoundException e) {
            // not found... file.exists might generate a sync problem
            return null;
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    public Object put(final Object key, final Object value) {
        final Object oldValue = get(key);
        final String filename = getName(key);
        writeFile(new File(baseDirectory, filename), value);
        return oldValue;
    }

    public Iterator iterator() {
        return new XmlMapEntriesIterator();
    }

    public int size() {
        return baseDirectory.list(filter).length;
    }

    public boolean containsKey(final Object key) {
        // faster lookup
        final File file = getFile(getName(key));
        return file.isFile();
    }

    public Object get(final Object key) {
        return readFile(getFile(getName(key)));
    }

    public Object remove(final Object key) {
        // faster lookup
        final File file = getFile(getName(key));
        Object value = null;
        if (file.isFile()) {
            value = readFile(file);
            file.delete();
        }
        return value;
    }

}
