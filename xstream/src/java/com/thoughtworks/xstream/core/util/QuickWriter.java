/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2014, 2023 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

import com.thoughtworks.xstream.io.StreamException;


public class QuickWriter implements Closeable {

    private final Writer writer;
    private final char[] buffer;
    private int pointer;

    public QuickWriter(final Writer writer) {
        this(writer, 1024);
    }

    public QuickWriter(final Writer writer, final int bufferSize) {
        this.writer = writer;
        buffer = new char[bufferSize];
    }

    public void write(final String str) {
        final int len = str.length();
        if (pointer + len > buffer.length) {
            flush();
            if (len > buffer.length) {
                raw(str.toCharArray());
                return;
            }
        }
        str.getChars(0, len, buffer, pointer);
        pointer += len;
    }

    public void write(final char c) {
        if (pointer + 1 > buffer.length) {
            flush();
            if (buffer.length == 0) {
                raw(c);
                return;
            }
        }
        buffer[pointer++] = c;
    }

    public void write(final char[] c) {
        final int len = c.length;
        if (pointer + len > buffer.length) {
            flush();
            if (len > buffer.length) {
                raw(c);
                return;
            }
        }
        System.arraycopy(c, 0, buffer, pointer, len);
        pointer += len;
    }

    public void flush() {
        try {
            writer.write(buffer, 0, pointer);
            pointer = 0;
            writer.flush();
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void close() {
        try {
            writer.write(buffer, 0, pointer);
            pointer = 0;
            writer.close();
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    private void raw(final char[] c) {
        try {
            writer.write(c);
            writer.flush();
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    private void raw(final char c) {
        try {
            writer.write(c);
            writer.flush();
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }
}
