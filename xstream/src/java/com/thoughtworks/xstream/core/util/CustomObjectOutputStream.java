package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.DataHolder;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class CustomObjectOutputStream extends ObjectOutputStream {

    private StreamCallback callback;

    private static final String DATA_HOLDER_KEY = CustomObjectOutputStream.class.getName();

    public static synchronized CustomObjectOutputStream getInstance(DataHolder whereFrom, StreamCallback callback) {
        try {
            CustomObjectOutputStream result = (CustomObjectOutputStream) whereFrom.get(DATA_HOLDER_KEY);
            if (result == null) {
                result = new CustomObjectOutputStream(callback);
                whereFrom.put(DATA_HOLDER_KEY, result);
            } else {
                result.setCallback(callback);
            }
            return result;
        } catch (IOException e) {
            throw new ConversionException("Cannot create CustomObjectStream", e);
        }
    }

    public static interface StreamCallback {
        void writeToStream(Object object);
        void defaultWriteObject();
    }

    /**
     * Warning, this object is expensive to create (due to functionality inherited from superclass).
     * Use the static fetch() method instead, wherever possible.
     *
     * @see #getInstance(com.thoughtworks.xstream.converters.DataHolder, com.thoughtworks.xstream.core.util.CustomObjectOutputStream.StreamCallback)
     */
    public CustomObjectOutputStream(StreamCallback callback) throws IOException, SecurityException {
        this.callback = callback;
    }

    /**
     * Allows the CustomObjectOutputStream (which is expensive to create) to be reused.
     */
    public void setCallback(StreamCallback callback) {
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
