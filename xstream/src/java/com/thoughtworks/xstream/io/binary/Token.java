package com.thoughtworks.xstream.io.binary;

import com.thoughtworks.xstream.io.StreamException;

import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;

/**
 * Represents the Tokens stored in the binary stream used by
 * {@link BinaryStreamReader} and {@link BinaryStreamWriter}.
 * <p/>
 * <p>A token consists of a type and (depending on this type)
 * it may additionally have an ID (positive long number)
 * and/or a value (String).</p>
 * <p/>
 * <p>The first byte of the token represents how many subsequent
 * bytes are used by the ID.
 *
 * @author Joe Walnes
 * @see BinaryStreamReader
 * @see BinaryStreamWriter
 * @since 1.2
 */
public abstract class Token {

    private static final byte TYPE_MASK = 0x7;
    public static final byte TYPE_VERSION = 0x1;
    public static final byte TYPE_MAP_ID_TO_VALUE = 0x2;
    public static final byte TYPE_START_NODE = 0x3;
    public static final byte TYPE_END_NODE = 0x4;
    public static final byte TYPE_ATTRIBUTE = 0x5;
    public static final byte TYPE_VALUE = 0x6;

    private static final byte ID_MASK = 0x38;
    private static final byte ID_ONE_BYTE = 0x08;
    private static final byte ID_TWO_BYTES = 0x10;
    private static final byte ID_FOUR_BYTES = 0x18;
    private static final byte ID_EIGHT_BYTES = 0x20;

    private final byte type;

    protected long id = -1;
    protected String value;

    public Token(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return getClass().getName() + " [id=" + id + ", value='" + value + "']";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Token token = (Token) o;

        if (id != token.id) return false;
        if (type != token.type) return false;
        return !(value != null ? !value.equals(token.value) : token.value != null);

    }

    public int hashCode() {
        int result;
        result = (int) type;
        result = 29 * result + (int) (id ^ (id >>> 32));
        result = 29 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public abstract void writeTo(DataOutput out, byte idType) throws IOException;

    public abstract void readFrom(DataInput in, byte idType) throws IOException;

    protected void writeId(DataOutput out, long id, byte idType) throws IOException {
        if (id < 0) {
            throw new IOException("id must not be negative " + id);
        }
        switch (idType) {
            case ID_ONE_BYTE:
                out.writeByte((byte) id + Byte.MIN_VALUE);
                break;
            case ID_TWO_BYTES:
                out.writeShort((short) id + Short.MIN_VALUE);
                break;
            case ID_FOUR_BYTES:
                out.writeInt((int) id + Integer.MIN_VALUE);
                break;
            case ID_EIGHT_BYTES:
                out.writeLong(id + Long.MIN_VALUE);
                break;
            default:
                throw new Error("Unknown idType " + idType);
        }
    }

    protected void writeString(DataOutput out, String string) throws IOException {
        out.writeUTF(string);
    }

    protected long readId(DataInput in, byte idType) throws IOException {
        switch (idType) {
            case ID_ONE_BYTE:
                return in.readByte() - Byte.MIN_VALUE;
            case ID_TWO_BYTES:
                return in.readShort() - Short.MIN_VALUE;
            case ID_FOUR_BYTES:
                return in.readInt() - Integer.MIN_VALUE;
            case ID_EIGHT_BYTES:
                return in.readLong() - Long.MIN_VALUE;
            default:
                throw new Error("Unknown idType " + idType);
        }
    }

    protected String readString(DataInput in) throws IOException {
        return in.readUTF();
    }

    public static class Formatter {

        public void write(DataOutput out, Token token) throws IOException {
            long id = token.getId();
            byte idType;
            if (id <= Byte.MAX_VALUE - Byte.MIN_VALUE) {
                idType = ID_ONE_BYTE;
            } else if (id <= Short.MAX_VALUE - Short.MIN_VALUE) {
                idType = ID_TWO_BYTES;
            } else if (id <= (long) Integer.MAX_VALUE - (long) Integer.MIN_VALUE) { // cast to long to prevent overflo
                idType = ID_FOUR_BYTES;
            } else {
                idType = ID_EIGHT_BYTES;
            }
            out.write(token.getType() + idType);
            token.writeTo(out, idType);
        }

        public Token read(DataInput in) throws IOException {
            byte nextByte = in.readByte();
            byte type = (byte) (nextByte & TYPE_MASK);
            byte idType = (byte) (nextByte & ID_MASK);
            Token token = contructToken(type);
            token.readFrom(in, idType);
            return token;
        }

        private Token contructToken(byte type) {
            switch (type) {
                case Token.TYPE_START_NODE:
                    return new StartNode();
                case Token.TYPE_MAP_ID_TO_VALUE:
                    return new MapIdToValue();
                case Token.TYPE_ATTRIBUTE:
                    return new Attribute();
                case Token.TYPE_END_NODE:
                    return new EndNode();
                case Token.TYPE_VALUE:
                    return new Value();
                default:
                    throw new StreamException("Unknown token type");
            }
        }
    }

    public static class MapIdToValue extends Token {

        public MapIdToValue(long id, String value) {
            super(TYPE_MAP_ID_TO_VALUE);
            this.id = id;
            this.value = value;
        }

        public MapIdToValue() {
            super(TYPE_MAP_ID_TO_VALUE);
        }

        public void writeTo(DataOutput out, byte idType) throws IOException {
            writeId(out, id, idType);
            writeString(out, value);
        }

        public void readFrom(DataInput in, byte idType) throws IOException {
            id = readId(in, idType);
            value = readString(in);
        }

    }

    public static class StartNode extends Token {

        public StartNode(long id) {
            super(TYPE_START_NODE);
            this.id = id;
        }

        public StartNode() {
            super(TYPE_START_NODE);
        }

        public void writeTo(DataOutput out, byte idType) throws IOException {
            writeId(out, id, idType);
        }

        public void readFrom(DataInput in, byte idType) throws IOException {
            id = readId(in, idType);
        }

    }

    public static class EndNode extends Token {

        public EndNode() {
            super(TYPE_END_NODE);
        }

        public void writeTo(DataOutput out, byte idType) throws IOException {
        }

        public void readFrom(DataInput in, byte idType) throws IOException {
        }

    }

    public static class Attribute extends Token {

        public Attribute(long id, String value) {
            super(TYPE_ATTRIBUTE);
            this.id = id;
            this.value = value;
        }

        public Attribute() {
            super(TYPE_ATTRIBUTE);
        }

        public void writeTo(DataOutput out, byte idType) throws IOException {
            writeId(out, id, idType);
            writeString(out, value);
        }

        public void readFrom(DataInput in, byte idType) throws IOException {
            this.id = readId(in, idType);
            this.value = readString(in);
        }

    }

    public static class Value extends Token {

        public Value(String value) {
            super(TYPE_VALUE);
            this.value = value;
        }

        public Value() {
            super(TYPE_VALUE);
        }

        public void writeTo(DataOutput out, byte idType) throws IOException {
            writeString(out, value);
        }

        public void readFrom(DataInput in, byte idType) throws IOException {
            value = readString(in);
        }

    }

}
