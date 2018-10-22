/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 06. August 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.thoughtworks.xstream.core.StringCodec;


/**
 * Encodes binary data to plain text as Base64.
 * <p>
 * Despite there being a gazillion other Base64 implementations out there, this has been written as part of XStream as
 * it forms a core part but is too trivial to warrant an extra dependency. Recent Java Runtimes (since Java 6) provide
 * an own Base64 codec though.
 * </p>
 * <p>
 * By default it will not insert line breaks to support Base64 values also as attribute values. However, the standard as
 * described in <a href="http://www.freesoft.org/CIE/RFC/1521/7.htm">RFC 1521, section 5.2</a> requires line breaks,
 * allowing other Base64 tools to manipulate the data. You can configure the Base64Encoder to be RFC compliant.
 * </p>
 *
 * @author Joe Walnes
 */
public class Base64Encoder implements StringCodec {

    // Here's how encoding works:
    //
    // 1) Incoming bytes are broken up into groups of 3 (each byte having 8 bits).
    //
    // 2) The combined 24 bits (3 * 8) are split into 4 groups of 6 bits.
    //
    // Input:
    // |------||------||------| (3 values each with 8 bits)
    // 101010101010101010101010
    // Output:
    // |----||----||----||----| (4 values each with 6 bits)
    //
    // 3) Each of these 4 groups of 6 bits are converted back to a number, which will fall in the range of 0 - 63.
    //
    // 4) Each of these 4 numbers are converted to an alphanumeric char in a specified mapping table, to create
    // a 4 character string.
    //
    // 5) This is repeated for all groups of three bytes.
    //
    // 6) Special padding is done at the end of the stream using the '=' char.

    private static final char[] SIXTY_FOUR_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        .toCharArray();
    private static final int[] REVERSE_MAPPING = new int[123];
    private final boolean lineBreaks;

    static {
        for (int i = 0; i < SIXTY_FOUR_CHARS.length; i++) {
            REVERSE_MAPPING[SIXTY_FOUR_CHARS[i]] = i + 1;
        }
    }

    /**
     * Constructs a Base64Encoder.
     * <p>
     * The encoder will not insert any line breaks.
     * </p>
     *
     * @since 1.4.11
     */
    public Base64Encoder() {
        this(false);
    }

    /**
     * Constructs a Base64Encoder.
     *
     * @param lineBreaks flag to insert line breaks
     * @since 1.4.11
     */
    public Base64Encoder(final boolean lineBreaks) {
        this.lineBreaks = lineBreaks;
    }

    public String encode(final byte[] input) {
        final int stringSize = computeResultingStringSize(input);
        final StringBuffer result = new StringBuffer(stringSize);
        int outputCharCount = 0;
        for (int i = 0; i < input.length; i += 3) {
            final int remaining = Math.min(3, input.length - i);
            final int oneBigNumber = (input[i] & 0xff) << 16
                | (remaining <= 1 ? 0 : input[i + 1] & 0xff) << 8
                | (remaining <= 2 ? 0 : input[i + 2] & 0xff);
            for (int j = 0; j < 4; j++) {
                result.append(remaining + 1 > j ? SIXTY_FOUR_CHARS[0x3f & oneBigNumber >> 6 * (3 - j)] : '=');
            }
            if (lineBreaks && (outputCharCount += 4) % 76 == 0 && i + 3 < input.length) {
                result.append('\n');
            }
        }
        final String s = result.toString();
        return s;
    }

    // package private for testing purpose
    int computeResultingStringSize(final byte[] input) {
        int stringSize = input.length / 3 + (input.length % 3 == 0 ? 0 : 1);
        stringSize *= 4;
        if (lineBreaks) {
            stringSize += stringSize / 76;
        }
        return stringSize;
    }

    public byte[] decode(final String input) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final StringReader in = new StringReader(input);
            for (int i = 0; i < input.length(); i += 4) {
                final int a[] = {mapCharToInt(in), mapCharToInt(in), mapCharToInt(in), mapCharToInt(in)};
                final int oneBigNumber = (a[0] & 0x3f) << 18 | (a[1] & 0x3f) << 12 | (a[2] & 0x3f) << 6 | a[3] & 0x3f;
                for (int j = 0; j < 3; j++) {
                    if (a[j + 1] >= 0) {
                        out.write(0xff & oneBigNumber >> 8 * (2 - j));
                    }
                }
            }
            return out.toByteArray();
        } catch (final IOException e) {
            throw new Error(e + ": " + e.getMessage());
        }
    }

    private int mapCharToInt(final Reader input) throws IOException {
        int c;
        while ((c = input.read()) != -1) {
            final int result = REVERSE_MAPPING[c];
            if (result != 0) {
                return result - 1;
            }
            if (c == '=') {
                return -1;
            }
        }
        return -1;
    }
}
