/*
 * Copyright (C) 2008, 2014, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 18. November 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.persistence;

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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Abstract base class for file based persistence strategies.
 *
 * @author Guilherme Silveira
 * @author Joerg Schaible
 * @since 1.3.1
 */
public abstract class AbstractFilePersistenceStrategy<K, V> implements PersistenceStrategy<K, V> {

    private final FilenameFilter filter;
    private final File baseDirectory;
    private final String encoding;
    private final transient XStream xstream;

    public AbstractFilePersistenceStrategy(final File baseDirectory, final XStream xstream, final String encoding) {
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
    protected abstract K extractKey(String name);

    /**
     * Given a key, the escape method returns the filename which shall be used.
     *
     * @param key the key
     * @return the desired and escaped filename
     */
    protected abstract String getName(Object key);

    protected class ValidFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(final File dir, final String name) {
            return new File(dir, name).isFile() && isValid(dir, name);
        }
    }

    protected class XmlMapEntriesIterator implements Iterator<Map.Entry<K, V>> {

        private final File[] files = baseDirectory.listFiles(filter);

        private int position = -1;

        private File current = null;

        @Override
        public boolean hasNext() {
            return position + 1 < files.length;
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }
            // removes without loading
            current.delete();
        }

        @Override
        public Map.Entry<K, V> next() {
            return new Map.Entry<K, V>() {
                private final File file = current = files[++position];
                private final K key = extractKey(file.getName());

                @Override
                public K getKey() {
                    return key;
                }

                @Override
                public V getValue() {
                    return readFile(file);
                }

                @Override
                public V setValue(final V value) {
                    return put(key, value);
                }

                @Override
                public int hashCode() {
                    final V value = getValue();
                    return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
                }

                @Override
                public boolean equals(final Object obj) {
                    if (!(obj instanceof Map.Entry<?, ?>)) {
                        return false;
                    }
                    @SuppressWarnings("unchecked")
                    final Map.Entry<K, V> e2 = (Map.Entry<K, V>)obj;
                    final K key2 = e2.getKey();
                    if (key == null ? key2 == null : key.equals(key2)) {
                        final V value = getValue();
                        final V value2 = e2.getValue();
                        return value == null ? value2 == null : value.equals(value2);
                    }
                    return false;
                }
            };
        }
    }

    private void writeFile(final File file, final Object value) {
        try {
            final FileOutputStream out = new FileOutputStream(file);
            try (final Writer writer = encoding != null
                ? new OutputStreamWriter(out, encoding)
                : new OutputStreamWriter(out)) {
                xstream.toXML(value, writer);
            }
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    private File getFile(final String filename) {
        return new File(baseDirectory, filename);
    }

    private V readFile(final File file) {
        try {
            final FileInputStream in = new FileInputStream(file);
            try (final Reader reader = encoding != null
                ? new InputStreamReader(in, encoding)
                : new InputStreamReader(in)) {
                @SuppressWarnings("unchecked")
                final V value = (V)xstream.fromXML(reader);
                return value;
            }
        } catch (final FileNotFoundException e) {
            // not found... file.exists might generate a sync problem
            return null;
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public V put(final K key, final V value) {
        final V oldValue = get(key);
        final String filename = getName(key);
        writeFile(new File(baseDirectory, filename), value);
        return oldValue;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new XmlMapEntriesIterator();
    }

    @Override
    public int size() {
        return baseDirectory.list(filter).length;
    }

    public boolean containsKey(final K key) {
        // faster lookup
        final File file = getFile(getName(key));
        return file.isFile();
    }

    @Override
    public V get(final Object key) {
        return readFile(getFile(getName(key)));
    }

    @Override
    public V remove(final Object key) {
        // faster lookup
        final File file = getFile(getName(key));
        V value = null;
        if (file.isFile()) {
            value = readFile(file);
            file.delete();
        }
        return value;
    }

}
