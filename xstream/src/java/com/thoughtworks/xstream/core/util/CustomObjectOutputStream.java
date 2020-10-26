/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2016, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 23. August 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.io.StreamException;


public class CustomObjectOutputStream extends ObjectOutputStream {

    private final FastStack<StreamCallback> callbacks = new FastStack<>(1);
    private final FastStack<CustomPutField> customFields = new FastStack<>(1);

    private static final String DATA_HOLDER_KEY = CustomObjectOutputStream.class.getName();

    public static synchronized CustomObjectOutputStream getInstance(final DataHolder whereFrom,
            final StreamCallback callback) {
        try {
            CustomObjectOutputStream result = (CustomObjectOutputStream)whereFrom.get(DATA_HOLDER_KEY);
            if (result == null) {
                result = new CustomObjectOutputStream(whereFrom, callback);
            } else {
                result.pushCallback(callback);
            }
            return result;
        } catch (final SecurityException e) {
            throw new ObjectAccessException("Cannot create CustomObjectStream", e);
        } catch (final IOException e) {
            throw new StreamException("Cannot create CustomObjectStream", e);
        }
    }

    public static interface StreamCallback {
        void writeToStream(Object object) throws IOException;

        void writeFieldsToStream(Map<String, Object> fields) throws IOException;

        void defaultWriteObject() throws IOException;

        void flush() throws IOException;

        void close() throws IOException;
    }

    /**
     * Warning, this object is expensive to create (due to functionality inherited from superclass). Use the static
     * fetch() method instead, wherever possible.
     *
     * @see #getInstance(com.thoughtworks.xstream.converters.DataHolder,
     *      com.thoughtworks.xstream.core.util.CustomObjectOutputStream.StreamCallback)
     */
    public CustomObjectOutputStream(final DataHolder dataHolder, final StreamCallback callback)
            throws IOException, SecurityException {
        callbacks.push(callback);
        if (dataHolder != null) {
            dataHolder.put(DATA_HOLDER_KEY, this);
        }
    }

    /**
     * Allows the CustomObjectOutputStream (which is expensive to create) to be reused.
     */
    public void pushCallback(final StreamCallback callback) {
        callbacks.push(callback);
    }

    public StreamCallback popCallback() {
        return callbacks.pop();
    }

    public StreamCallback peekCallback() {
        return callbacks.peek();
    }

    /*** Methods to delegate to callback ***/

    @Override
    public void defaultWriteObject() throws IOException {
        peekCallback().defaultWriteObject();
    }

    @Override
    protected void writeObjectOverride(final Object obj) throws IOException {
        peekCallback().writeToStream(obj);
    }

    @Override
    public void writeBoolean(final boolean val) throws IOException {
        peekCallback().writeToStream(Boolean.valueOf(val));
    }

    @Override
    public void writeByte(final int val) throws IOException {
        peekCallback().writeToStream(Byte.valueOf((byte)val));
    }

    @Override
    public void writeInt(final int val) throws IOException {
        peekCallback().writeToStream(Integer.valueOf(val));
    }

    @Override
    public void writeChar(final int val) throws IOException {
        peekCallback().writeToStream(Character.valueOf((char)val));
    }

    @Override
    public void writeDouble(final double val) throws IOException {
        peekCallback().writeToStream(Double.valueOf(val));
    }

    @Override
    public void writeFloat(final float val) throws IOException {
        peekCallback().writeToStream(Float.valueOf(val));
    }

    @Override
    public void writeLong(final long val) throws IOException {
        peekCallback().writeToStream(Long.valueOf(val));
    }

    @Override
    public void writeShort(final int val) throws IOException {
        peekCallback().writeToStream(Short.valueOf((short)val));
    }

    @Override
    public void write(final byte[] buf) throws IOException {
        peekCallback().writeToStream(buf);
    }

    @Override
    public void writeChars(final String str) throws IOException {
        peekCallback().writeToStream(str.toCharArray());
    }

    @Override
    public void writeUTF(final String str) throws IOException {
        peekCallback().writeToStream(str);
    }

    @Override
    public void write(final int val) throws IOException {
        peekCallback().writeToStream(Byte.valueOf((byte)val));
    }

    @Override
    public void write(final byte[] buf, final int off, final int len) throws IOException {
        final byte[] b = new byte[len];
        System.arraycopy(buf, off, b, 0, len);
        peekCallback().writeToStream(b);
    }

    @Override
    public void flush() throws IOException {
        peekCallback().flush();
    }

    @Override
    public void close() throws IOException {
        peekCallback().close();
    }

    @Override
    public PutField putFields() {
        final CustomPutField result = new CustomPutField();
        customFields.push(result);
        return result;
    }

    @Override
    public void writeFields() throws IOException {
        final CustomPutField customPutField = customFields.pop();
        peekCallback().writeFieldsToStream(customPutField.asMap());
    }

    private class CustomPutField extends PutField {

        private final Map<String, Object> fields = new LinkedHashMap<>();

        public Map<String, Object> asMap() {
            return fields;
        }

        @Override
        public void write(final ObjectOutput out) throws IOException {
            peekCallback().writeToStream(asMap());
        }

        @Override
        public void put(final String name, final Object val) {
            fields.put(name, val);
        }

        @Override
        public void put(final String name, final byte val) {
            put(name, Byte.valueOf(val));
        }

        @Override
        public void put(final String name, final char val) {
            put(name, Character.valueOf(val));
        }

        @Override
        public void put(final String name, final double val) {
            put(name, Double.valueOf(val));
        }

        @Override
        public void put(final String name, final float val) {
            put(name, Float.valueOf(val));
        }

        @Override
        public void put(final String name, final int val) {
            put(name, Integer.valueOf(val));
        }

        @Override
        public void put(final String name, final long val) {
            put(name, Long.valueOf(val));
        }

        @Override
        public void put(final String name, final short val) {
            put(name, Short.valueOf(val));
        }

        @Override
        public void put(final String name, final boolean val) {
            put(name, Boolean.valueOf(val));
        }

    }

    /****** Unsupported methods ******/

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void useProtocolVersion(final int version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeBytes(final String str) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeUnshared(final Object obj) {
        throw new UnsupportedOperationException();
    }

}
