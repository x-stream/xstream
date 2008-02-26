/*
 * Copyright (C) 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. September 2007 by Joerg Schaible.
 */

package com.thoughtworks.xstream.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * A {@link Reader} that evaluates the XML header. It selects its encoding based on the encoding read with the XML
 * header of the provided {@link InputStream}. The default encoding is <em>UTF-8</em> and the version is 1.0 if the
 * stream does not contain an XML header or the attributes are not set within the header.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public final class XmlHeaderAwareReader extends Reader {

    private final InputStreamReader reader;
    private final double version;

    private static final String KEY_ENCODING = "encoding";
    private static final String KEY_VERSION = "version";

    private static final String XML_TOKEN = "?xml";

    private static final int STATE_START = 0;
    private static final int STATE_AWAIT_XML_HEADER = 1;
    private static final int STATE_ATTR_NAME = 2;
    private static final int STATE_ATTR_VALUE = 3;

    /**
     * Constructs an XmlHeaderAwareReader.
     * 
     * @param in the {@link InputStream}
     * @throws UnsupportedEncodingException if the encoding is not supported
     * @throws IOException occurred while reading the XML header
     * @since 1.3
     */
    public XmlHeaderAwareReader(final InputStream in) throws UnsupportedEncodingException, IOException {
        final PushbackInputStream pin = in instanceof PushbackInputStream
                                                                         ? (PushbackInputStream)in
                                                                         : new PushbackInputStream(in, 64);
        final Map header = getHeader(pin);
        version = Double.parseDouble((String)header.get(KEY_VERSION));
        reader = new InputStreamReader(pin, (String)header.get(KEY_ENCODING));
    }

    private Map getHeader(final PushbackInputStream in) throws IOException {
        final Map header = new HashMap();
        header.put(KEY_ENCODING, "utf-8");
        header.put(KEY_VERSION, "1.0");

        int state = STATE_START;
        final ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        int i = 0;
        char ch = 0;
        char valueEnd = 0;
        final StringBuffer name = new StringBuffer();
        final StringBuffer value = new StringBuffer();
        boolean escape = false;
        while (i != -1 && (i = in.read()) != -1) {
            out.write(i);
            ch = (char)i;
            switch (state) {
            case STATE_START:
                if (!Character.isWhitespace(ch)) {
                    if (ch == '<') {
                        state = STATE_AWAIT_XML_HEADER;
                    } else {
                        i = -1;
                    }
                }
                break;
            case STATE_AWAIT_XML_HEADER:
                if (!Character.isWhitespace(ch)) {
                    name.append(Character.toLowerCase(ch));
                    if (!XML_TOKEN.startsWith(name.substring(0))) {
                        i = -1;
                    }
                } else {
                    if (name.toString().equals(XML_TOKEN)) {
                        state = STATE_ATTR_NAME;
                        name.setLength(0);
                    } else {
                        i = -1;
                    }
                }
                break;
            case STATE_ATTR_NAME:
                if (!Character.isWhitespace(ch)) {
                    if (ch == '=') {
                        state = STATE_ATTR_VALUE;
                    } else {
                        ch = Character.toLowerCase(ch);
                        if (Character.isLetter(ch)) {
                            name.append(ch);
                        } else {
                            i = -1;
                        }
                    }
                } else if (name.length() > 0) {
                    i = -1;
                }
                break;
            case STATE_ATTR_VALUE:
                if (valueEnd == 0) {
                    if (ch == '"' || ch == '\'') {
                        valueEnd = ch;
                    } else {
                        i = -1;
                    }
                } else {
                    if (ch == '\\' && !escape) {
                        escape = true;
                        break;
                    }
                    if (ch == valueEnd && !escape) {
                        valueEnd = 0;
                        state = STATE_ATTR_NAME;
                        header.put(name.toString(), value.toString());
                        name.setLength(0);
                        value.setLength(0);
                    } else {
                        escape = false;
                        if (ch != '\n') {
                            value.append(ch);
                        } else {
                            i = -1;
                        }
                    }
                }
                break;
            }
        }

        in.unread(out.toByteArray());
        return header;
    }

    /**
     * @see InputStreamReader#getEncoding()
     * @since 1.3
     */
    public String getEncoding() {
        return reader.getEncoding();
    }

    /**
     * @see InputStreamReader#getEncoding()
     * @since 1.3
     */
    public double getVersion() {
        return version;
    }

    /**
     * @see java.io.Reader#mark(int)
     */
    public void mark(final int readAheadLimit) throws IOException {
        reader.mark(readAheadLimit);
    }

    /**
     * @see java.io.Reader#markSupported()
     */
    public boolean markSupported() {
        return reader.markSupported();
    }

    /**
     * @see java.io.Reader#read()
     */
    public int read() throws IOException {
        return reader.read();
    }

    /**
     * @see java.io.Reader#read(char[], int, int)
     */
    public int read(final char[] cbuf, final int offset, final int length) throws IOException {
        return reader.read(cbuf, offset, length);
    }

    /**
     * @see java.io.Reader#read(char[])
     */
    public int read(final char[] cbuf) throws IOException {
        return reader.read(cbuf);
    }

// TODO: This is JDK 1.5    
//    public int read(final CharBuffer target) throws IOException {
//        return reader.read(target);
//    }

    /**
     * @see java.io.Reader#ready()
     */
    public boolean ready() throws IOException {
        return reader.ready();
    }

    /**
     * @see java.io.Reader#reset()
     */
    public void reset() throws IOException {
        reader.reset();
    }

    /**
     * @see java.io.Reader#skip(long)
     */
    public long skip(final long n) throws IOException {
        return reader.skip(n);
    }

    /**
     * @see java.io.Reader#close()
     */
    public void close() throws IOException {
        reader.close();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object obj) {
        return reader.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return reader.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return reader.toString();
    }
}
