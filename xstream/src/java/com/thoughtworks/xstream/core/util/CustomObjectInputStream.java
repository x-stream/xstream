package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.DataHolder;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectStreamClass;
import java.util.Map;

public class CustomObjectInputStream extends ObjectInputStream {

    private StreamCallback callback;

    private static final String DATA_HOLDER_KEY = CustomObjectInputStream.class.getName();

    public static interface StreamCallback {
        Object readFromStream() throws IOException;
        Map readFieldsFromStream() throws IOException;
        void defaultReadObject() throws IOException;
        void close() throws IOException;
    }

    public static synchronized CustomObjectInputStream getInstance(DataHolder whereFrom, CustomObjectInputStream.StreamCallback callback) {
        try {
            CustomObjectInputStream result = (CustomObjectInputStream) whereFrom.get(DATA_HOLDER_KEY);
            if (result == null) {
                result = new CustomObjectInputStream(callback);
                whereFrom.put(DATA_HOLDER_KEY, result);
            } else {
                result.setCallback(callback);
            }
            return result;
        } catch (IOException e) {
            throw new ConversionException("Cannot create CustomObjectStream", e);
        }
    }

    /**
     * Warning, this object is expensive to create (due to functionality inherited from superclass).
     * Use the static fetch() method instead, wherever possible.
     *
     * @see #getInstance(com.thoughtworks.xstream.converters.DataHolder, com.thoughtworks.xstream.core.util.CustomObjectInputStream.StreamCallback)
     */
    public CustomObjectInputStream(StreamCallback callback) throws IOException, SecurityException {
        super();
        this.callback = callback;
    }

    /**
     * Allows the CustomObjectInputStream (which is expensive to create) to be reused.
     */
    public void setCallback(StreamCallback callback) {
        this.callback = callback;
    }

    public void defaultReadObject() throws IOException, ClassNotFoundException {
        callback.defaultReadObject();
    }

    protected Object readObjectOverride() throws IOException, ClassNotFoundException {
        return callback.readFromStream();
    }

    public boolean readBoolean() throws IOException {
        return ((Boolean)callback.readFromStream()).booleanValue();
    }

    public byte readByte() throws IOException {
        return ((Byte)callback.readFromStream()).byteValue();
    }

    public int readInt() throws IOException {
        return ((Integer)callback.readFromStream()).intValue();
    }

    public char readChar() throws IOException {
        return ((Character)callback.readFromStream()).charValue();
    }

    public float readFloat() throws IOException {
        return ((Float)callback.readFromStream()).floatValue();
    }

    public double readDouble() throws IOException {
        return ((Double)callback.readFromStream()).doubleValue();
    }

    public long readLong() throws IOException {
        return ((Long)callback.readFromStream()).longValue();
    }

    public short readShort() throws IOException {
        return ((Short)callback.readFromStream()).shortValue();
    }

    public String readUTF() throws IOException {
        return (String) callback.readFromStream();
    }

    public void readFully(byte[] buf) throws IOException {
        readFully(buf, 0, buf.length);
    }

    public void readFully(byte[] buf, int off, int len) throws IOException {
        byte[] b = (byte[])callback.readFromStream();
        for(int i = 0; i < len; i++) {
            buf[i + off] = b[i];
        }
    }

    public GetField readFields() throws IOException, ClassNotFoundException {
        return new CustomGetField(callback.readFieldsFromStream());
    }

    private class CustomGetField extends GetField {

        private Map fields;

        public CustomGetField(Map fields) {
            this.fields = fields;
        }

        public ObjectStreamClass getObjectStreamClass() {
            throw new UnsupportedOperationException();
        }

        private Object get(String name) {
            return fields.get(name);
        }

        public boolean defaulted(String name) throws IOException {
            return !fields.containsKey(name);
        }

        public byte get(String name, byte val) throws IOException {
            return defaulted(name) ? val : ((Byte)get(name)).byteValue();
        }

        public char get(String name, char val) throws IOException {
            return defaulted(name) ? val : ((Character)get(name)).charValue();
        }

        public double get(String name, double val) throws IOException {
            return defaulted(name) ? val : ((Double)get(name)).doubleValue();
        }

        public float get(String name, float val) throws IOException {
            return defaulted(name) ? val : ((Float)get(name)).floatValue();
        }

        public int get(String name, int val) throws IOException {
            return defaulted(name) ? val : ((Integer)get(name)).intValue();
        }

        public long get(String name, long val) throws IOException {
            return defaulted(name) ? val : ((Long)get(name)).longValue();
        }

        public short get(String name, short val) throws IOException {
            return defaulted(name) ? val : ((Short)get(name)).shortValue();
        }

        public boolean get(String name, boolean val) throws IOException {
            return defaulted(name) ? val : ((Boolean)get(name)).booleanValue();
        }

        public Object get(String name, Object val) throws IOException {
            return defaulted(name) ? val : get(name);
        }

    }

    /****** Currently missing from implementation ******/

    public void registerValidation(ObjectInputValidation obj, int prio) throws NotActiveException, InvalidObjectException {
        super.registerValidation(obj, prio);
    }

    /****** Unsupported methods ******/

    public int available() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void close() throws IOException {
        callback.close();
    }

    public int readUnsignedByte() throws IOException {
        throw new UnsupportedOperationException();
    }

    public String readLine() throws IOException {
        throw new UnsupportedOperationException();
    }

    public Object readUnshared() throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException();
    }

    public int readUnsignedShort() throws IOException {
        throw new UnsupportedOperationException();
    }

    public int read() throws IOException {
        throw new UnsupportedOperationException();
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        throw new UnsupportedOperationException();
    }

    public int skipBytes(int len) throws IOException {
        throw new UnsupportedOperationException();
    }

    public int read(byte b[]) throws IOException {
        throw new UnsupportedOperationException();
    }

    public long skip(long n) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    public boolean markSupported() {
        return false;
    }

}
