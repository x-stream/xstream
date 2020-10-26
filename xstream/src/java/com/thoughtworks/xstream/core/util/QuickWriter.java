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
        if (pointer + len >= buffer.length) {
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
        if (pointer + 1 >= buffer.length) {
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
        if (pointer + len >= buffer.length) {
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
