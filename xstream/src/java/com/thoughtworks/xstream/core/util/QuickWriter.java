package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.io.StreamException;

import java.io.IOException;
import java.io.Writer;

public class QuickWriter {

    private final Writer writer;
    private char[] buffer;
    private int pointer;

    public QuickWriter(Writer writer) {
        this.writer = writer;
        buffer = new char[1024];
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

    private void raw(char[] c) {
        try {
            writer.write(c);
            writer.flush();
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }
}
