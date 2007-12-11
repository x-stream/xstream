/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.io.StreamException;

import java.io.IOException;
import java.io.Writer;

public class QuickWriter {

    private final Writer writer;
    private char[] buffer;
    private int pointer;

    public QuickWriter(Writer writer) {
        this(writer, 1024);
    }

    public QuickWriter(Writer writer, int bufferSize) {
        this.writer = writer;
        buffer = new char[bufferSize];
    }

    public void write(String str) {
        int len = str.length();
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

    public void write(char c) {
        if (pointer + 1 >= buffer.length) {
            flush();
        }
        buffer[pointer++] = c;
    }

    public void write(char[] c) {
        int len = c.length;
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
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    public void close() {
        try {
            writer.write(buffer, 0, pointer);
            pointer = 0;
            writer.close();
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    private void raw(char[] c) {
        try {
            writer.write(c);
            writer.flush();
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }
}
