package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.ConversionException;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;

public class CustomObjectInputStream extends ObjectInputStream {

    private StreamCallback callback;

    public static CustomObjectInputStream create(StreamCallback callback) {
        try {
            return new CustomObjectInputStream(callback);
        } catch (IOException e) {
            throw new ConversionException("Cannot create CustomObjectStream: " + e.getMessage());
        }
    }

    /**
     * Allows the CustomObjectInputStream (which is expensive to create) to be reused.
     */
    public void setCallback(StreamCallback callback) {
        this.callback = callback;
    }

    public CustomObjectInputStream(StreamCallback callback) throws IOException, SecurityException {
        super();
        this.callback = callback;
    }

    public static interface StreamCallback {
        Object deserialize();
        void defaultReadObject();
    }

    public void defaultReadObject() throws IOException, ClassNotFoundException {
        callback.defaultReadObject();
    }

    protected Object readObjectOverride() throws IOException, ClassNotFoundException {
        return callback.deserialize();
    }

    public boolean readBoolean() throws IOException {
        return ((Boolean)callback.deserialize()).booleanValue();
    }

    public byte readByte() throws IOException {
        return ((Byte)callback.deserialize()).byteValue();
    }

    public int readInt() throws IOException {
        return ((Integer)callback.deserialize()).intValue();
    }

    public char readChar() throws IOException {
        return ((Character)callback.deserialize()).charValue();
    }

    public float readFloat() throws IOException {
        return ((Float)callback.deserialize()).floatValue();
    }

    public double readDouble() throws IOException {
        return ((Double)callback.deserialize()).doubleValue();
    }

    public long readLong() throws IOException {
        return ((Long)callback.deserialize()).longValue();
    }

    public short readShort() throws IOException {
        return ((Short)callback.deserialize()).shortValue();
    }

    public String readUTF() throws IOException {
        return (String) callback.deserialize();
    }

    public void readFully(byte[] buf) throws IOException {
        readFully(buf, 0, buf.length);
    }

    public void readFully(byte[] buf, int off, int len) throws IOException {
        byte[] b = (byte[])callback.deserialize();
        for(int i = 0; i < len; i++) {
            buf[i + off] = b[i];
        }
    }

    /****** Methods currently missing from implementation ******/

    public GetField readFields() throws IOException, ClassNotFoundException {
        return super.readFields();
    }

    public void registerValidation(ObjectInputValidation obj, int prio) throws NotActiveException, InvalidObjectException {
        super.registerValidation(obj, prio);
    }

    /****** Unsupported methods ******/

    public int available() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void close() throws IOException {
        throw new UnsupportedOperationException();
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
