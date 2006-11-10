package com.thoughtworks.xstream.io.binary;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @since 1.2
 */
public class BinaryStreamWriter implements ExtendedHierarchicalStreamWriter {

    private final IdRegistry idRegistry = new IdRegistry();
    private final DataOutputStream out;
    private final Token.Formatter tokenFormatter = new Token.Formatter();

    public BinaryStreamWriter(OutputStream outputStream) {
        out = new DataOutputStream(outputStream);
    }

    public void startNode(String name) {
        write(new Token.StartNode(idRegistry.getId(name)));
    }

    public void startNode(String name, Class clazz) {
        startNode(name);
    }

    public void addAttribute(String name, String value) {
        write(new Token.Attribute(idRegistry.getId(name), value));
    }

    public void setValue(String text) {
        write(new Token.Value(text));
    }

    public void endNode() {
        write(new Token.EndNode());
    }

    public void flush() {
        try {
            out.flush();
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }

    private void write(Token token) {
        try {
            tokenFormatter.write(out, token);
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    private class IdRegistry {

        private long nextId = 0;
        private Map ids = new HashMap();

        public long getId(String value) {
            Long id = (Long) ids.get(value);
            if (id == null) {
                id = new Long(++nextId);
                ids.put(value, id);
                write(new Token.MapIdToValue(id.longValue(), value));
            }
            return id.longValue();
        }

    }
}
