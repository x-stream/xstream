package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.ConversionException;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class CustomObjectOutputStream extends ObjectOutputStream {

    private StreamCallback callback;

    public static CustomObjectOutputStream create(StreamCallback callback) {
        try {
            return new CustomObjectOutputStream(callback);
        } catch (IOException e) {
            throw new ConversionException("Cannot create CustomObjectStream: " + e.getMessage());
        }
    }

    /**
     * Allows the CustomObjectOutputStream (which is expensive to create) to be reused.
     */
    public void setCallback(StreamCallback callback) {
        this.callback = callback;
    }

    public static interface StreamCallback {
        void writeToStream(Object object);
        void defaultWriteObject();
    }

    protected CustomObjectOutputStream(StreamCallback callback) throws IOException, SecurityException {
        this.callback = callback;
    }

    /*** Methods to delegate to callback ***/

    public void defaultWriteObject() throws IOException {
        callback.defaultWriteObject();
    }

    protected void writeObjectOverride(Object obj) throws IOException {
        callback.writeToStream(obj);
    }

    public void writeBoolean(boolean val) throws IOException {
        callback.writeToStream(new Boolean(val));
    }

    public void writeByte(int val) throws IOException {
        callback.writeToStream(new Byte((byte) val));
    }

    public void writeInt(int val) throws IOException {
        callback.writeToStream(new Integer(val));
    }

    public void writeChar(int val) throws IOException {
        callback.writeToStream(new Character((char)val));
    }

    public void writeDouble(double val) throws IOException {
        callback.writeToStream(new Double(val));
    }

    public void writeFloat(float val) throws IOException {
        callback.writeToStream(new Float(val));
    }

    public void writeLong(long val) throws IOException {
        callback.writeToStream(new Long(val));
    }

    public void writeShort(int val) throws IOException {
        callback.writeToStream(new Short((short) val));
    }

    public void write(byte[] buf) throws IOException {
        callback.writeToStream(buf);
    }

    public void writeChars(String str) throws IOException {
        callback.writeToStream(str.toCharArray());
    }

    public void writeUTF(String str) throws IOException {
        callback.writeToStream(str);
    }

    public void write(int val) throws IOException {
        callback.writeToStream(new Byte((byte) val));
    }

    public void write(byte[] buf, int off, int len) throws IOException {
        byte[] b = new byte[len];
        for(int i = 0; i < len; i++) {
            b[i] = buf[i + off];
        }
        callback.writeToStream(b);
    }

    /****** Methods currently missing from implementation ******/

    public PutField putFields() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void writeFields() throws IOException {
        throw new UnsupportedOperationException();
    }

    /****** Unsupported methods ******/

    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void flush() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void useProtocolVersion(int version) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void writeBytes(String str) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void writeUnshared(Object obj) throws IOException {
        throw new UnsupportedOperationException();
    }

}
