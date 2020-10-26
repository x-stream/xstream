/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.persistence;

import java.io.File;

import com.thoughtworks.xstream.XStream;


/**
 * PersistenceStrategy to assign string based keys to objects persisted in files.
 * <p>
 * The file naming strategy is based on the key's type name and its toString method. It escapes non digit, non a-z and
 * A-Z characters. In order to change the escaping/unescaping algorithm, simply extend this class and rewrite its
 * getName/extractKey methods. Note, this implementation silently implies that the keys actually are Strings, since the
 * keys will be turned into string keys at deserialization time.
 * </p>
 * 
 * @author Guilherme Silveira
 * @deprecated As of 1.3.1, use FilePersistenceStrategy
 */
@Deprecated
public class FileStreamStrategy<V> extends AbstractFilePersistenceStrategy<String, V> implements
    StreamStrategy<String, V> {
    public FileStreamStrategy(final File baseDirectory) {
        this(baseDirectory, new XStream());
    }

    public FileStreamStrategy(final File baseDirectory, final XStream xstream) {
        super(baseDirectory, xstream, null);
    }

    /**
     * Given a filename, the unescape method returns the key which originated it.
     * 
     * @param name the filename
     * @return the original key
     */
    @Override
    protected String extractKey(final String name) {
        final String key = unescape(name.substring(0, name.length() - 4));
        return key.equals("\0") ? null : key;
    }

    protected String unescape(final String name) {
        final StringBuilder buffer = new StringBuilder();
        char lastC = '\uffff';
        int currentValue = -1;
        // do we have a regex master to do it?
        final char[] array = name.toCharArray();
        for (final char c : array) {
            if (c == '_' && currentValue != -1) {
                if (lastC == '_') {
                    buffer.append('_');
                } else {
                    buffer.append((char)currentValue);
                }
                currentValue = -1;
            } else if (c == '_') {
                currentValue = 0;
            } else if (currentValue != -1) {
                currentValue = currentValue * 16 + Integer.parseInt(String.valueOf(c), 16);
            } else {
                buffer.append(c);
            }
            lastC = c;
        }
        return buffer.toString();
    }

    /**
     * Given a key, the escape method returns the filename which shall be used.
     * 
     * @param key the key
     * @return the desired and escaped filename
     */
    @Override
    protected String getName(final Object key) {
        return escape(key == null ? "\0" : key.toString()) + ".xml";
    }

    protected String escape(final String key) {
        final StringBuilder buffer = new StringBuilder();
        final char[] array = key.toCharArray();
        for (final char c : array) {
            if (Character.isDigit(c) || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
                buffer.append(c);
            } else if (c == '_') {
                buffer.append("__");
            } else {
                buffer.append("_" + Integer.toHexString(c) + "_");
            }
        }
        return buffer.toString();
    }
}
