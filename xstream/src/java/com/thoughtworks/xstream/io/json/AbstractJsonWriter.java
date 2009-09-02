/*
 * Copyright (C) 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 20. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.json;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.AbstractWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * An abstract implementation of a writer that calls abstract methods to build JSON structures.
 * Note, that XStream's implicit collection feature is not compatible with any kind of JSON
 * syntax.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public abstract class AbstractJsonWriter extends AbstractWriter {
    /**
     * DROP_ROOT_MODE drops the JSON root node.
     * <p>
     * The root node is the first level of the JSON object i.e.
     * 
     * <pre>
     * { &quot;person&quot;: {
     *     &quot;name&quot;: &quot;Joe&quot;
     * }}
     * </pre>
     * 
     * will be written without root simply as
     * 
     * <pre>
     * {
     *     &quot;name&quot;: &quot;Joe&quot;
     * }
     * </pre>
     * 
     * Without a root node, the top level element might now also be an array. However, it is
     * possible to generate invalid JSON unless {@link #STRICT_MODE} is also set.
     * </p>
     * 
     * @since 1.3.1
     */
    public static final int DROP_ROOT_MODE = 1;
    /**
     * STRICT_MODE prevents invalid JSON for single value objects when dropping the root.
     * <p>
     * The mode is only useful in combination with the {@link #DROP_ROOT_MODE}. An object with a
     * single value as first node i.e.
     * 
     * <pre>
     * { &quot;name&quot;: &quot;Joe&quot; }
     * </pre>
     * 
     * is simply written as
     * 
     * <pre>
     * &quot;Joe&quot;
     * </pre>
     * 
     * However, this is no longer valid JSON. Therefore you can activate {@link #STRICT_MODE}
     * and a {@link ConversionException} is thrown instead.
     * </p>
     * 
     * @since 1.3.1
     */
    public static final int STRICT_MODE = 2;
    /**
     * EXPLICIT_MODE assures that all data has its explicit equivalent in the resulting JSON.
     * <p>
     * XStream is normally using attributes in XML that have no real equivalent in JSON. While
     * attributes for objects are written by default in JSON as a member with a name that is
     * prepended with an '@' symbol, this is not possible for arrays or members itself.
     * Additionally arrays will normally have only values, not members. The JSON writer will
     * insert in EXPLICIT_MODE additional objects where the attributes can be added normally and
     * the content (resp. the array or the elemen's value) is available in a member with name
     * '$'. Here an example of an string array with one member, where the array and the string
     * has an additional attribute 'id':
     * 
     * <pre>
     * {&quot;string-array&quot;:{&quot;@id&quot;:&quot;1&quot;,&quot;$&quot;:[{&quot;string&quot;:{&quot;@id&quot;:&quot;2&quot;,&quot;$&quot;:&quot;Joe&quot;}}]}}
     * </pre>
     * 
     * However, this format can be used to deserialize into Java again.
     * </p>
     * 
     * @since upcoming
     */
    public static final int EXPLICIT_MODE = 4;

    public static class Type {
        public static Type NULL = new Type();
        public static Type STRING = new Type();
        public static Type NUMBER = new Type();
        public static Type BOOLEAN = new Type();
    }

    private static class Status {
        public final static Status OBJECT = new Status("object");
        public final static Status PROPERTY = new Status("property");
        public final static Status VALUE = new Status("value");
        public final static Status END = new Status("end");
        public final static Status ARRAY = new Status("array");
        public final static Status ELEMENT = new Status("element");
        public final static Status VIRTUAL = new Status("virtual");

        private final String name;

        private Status(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    private static final List NUMBER_TYPES = Arrays.asList(new Class[]{
        byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class,
        Long.class, float.class, Float.class, double.class, Double.class});
    private int mode;
    private FastStack statusStack = new FastStack(32);
    private FastStack typeStack = new FastStack(16);

    /**
     * Construct a JSON writer.
     * 
     * @since upcoming
     */
    public AbstractJsonWriter() {
        this(new NoNameCoder());
    }

    /**
     * Construct a JSON writer with a special mode.
     * 
     * @param mode a bit mask of the mode constants
     * @since upcoming
     */
    public AbstractJsonWriter(int mode) {
        this(mode, new NoNameCoder());
    }

    /**
     * Construct a JSON writer with a special name coder.
     * 
     * @param nameCoder the name coder to use
     * @since upcoming
     */
    public AbstractJsonWriter(NameCoder nameCoder) {
        this(0, nameCoder);
    }

    /**
     * Construct a JSON writer with a special mode and name coder.
     * 
     * @param mode a bit mask of the mode constants
     * @param nameCoder the name coder to use
     * @since upcoming
     */
    public AbstractJsonWriter(int mode, NameCoder nameCoder) {
        super(nameCoder);
        this.mode = mode;
    }

    /**
     * {@inheritDoc}
     */
    public void startNode(String name, Class clazz) {
        if (statusStack.size() == 0) {
            statusStack.push(Status.OBJECT);
            if ((mode & DROP_ROOT_MODE) == 0) {
                startObject(name);
            }
        } else {
            Class type = (Class)typeStack.peek();
            Status status = (Status)statusStack.peek();
            if (isArray(type)) {
                if (status == Status.OBJECT
                    || status == Status.VIRTUAL
                    || status == Status.PROPERTY
                    || status == Status.VALUE) {
                    if (status == Status.PROPERTY || status == Status.VIRTUAL) {
                        nextElement();
                        addLabel("$");
                    }
                    if (status == Status.VALUE) {
                        statusStack.replaceSilently(Status.ARRAY);
                    } else {
                        statusStack.push(Status.ARRAY);
                    }
                    startArray();
                    statusStack.push(Status.OBJECT);
                    if ((mode & EXPLICIT_MODE) != 0) {
                        startObject(name);
                    }
                } else if (status == Status.END) {
                    if ((mode & EXPLICIT_MODE) != 0) {
                        endObject();
                    }
                    statusStack.popSilently();
                    status = (Status)statusStack.peek();
                    if (status == Status.ARRAY) {
                        statusStack.replaceSilently(Status.ELEMENT);
                    } else if (status != Status.ELEMENT) {
                        throw new IllegalStateException("Cannot add new array element");
                    }
                    nextElement();
                    statusStack.push(Status.OBJECT);
                    if ((mode & EXPLICIT_MODE) != 0) {
                        startObject(name);
                    }
                } else {
                    throw new IllegalStateException("Cannot start new array element");
                }
            } else if (status == Status.VALUE) {
                statusStack.replaceSilently(Status.OBJECT);
                startObject(name);
            } else if (status == Status.PROPERTY
                || status == Status.END
                || status == Status.VIRTUAL) {
                statusStack.replaceSilently(Status.PROPERTY);
                nextElement();
                addLabel(name);
            } else {
                throw new IllegalStateException("Cannot start new element");
            }
        }
        statusStack.push(Status.VALUE);
        typeStack.push(clazz == null ? String.class : clazz);
    }

    /**
     * {@inheritDoc}
     */
    public void startNode(String name) {
        startNode(name, String.class);
    }

    /**
     * {@inheritDoc}
     */
    public void addAttribute(String name, String value) {
        Class type = (Class)typeStack.peek();
        if ((mode & EXPLICIT_MODE) != 0 || !isArray(type)) {
            Status status = (Status)statusStack.peek();
            if (status == Status.VALUE) {
                statusStack.replaceSilently(Status.VIRTUAL);
                startObject("@" + name);
            } else if (status == Status.PROPERTY || status == Status.VIRTUAL) {
                nextElement();
                addLabel("@" + name);
            } else {
                throw new IllegalStateException("Cannot add attribute");
            }
            addValue(value, Type.STRING);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String text) {
        Status status = (Status)statusStack.peek();
        Class type = (Class)typeStack.peek();
        if (status == Status.PROPERTY || status == Status.VIRTUAL) {
            nextElement();
            addLabel("$");
            statusStack.replaceSilently(Status.END);
        } else if (status == Status.VALUE) {
            statusStack.popSilently();
            typeStack.popSilently();
            if ((mode & STRICT_MODE) != 0 && typeStack.size() == 0) {
                throw new ConversionException("Single value cannot be root element");
            }
        } else {
            throw new IllegalStateException("Cannot set value");
        }
        if ((type == Character.class || type == Character.TYPE) && "".equals(text)) {
            text = "\u0000";
        }
        addValue(text, getType(type));
    }

    /**
     * {@inheritDoc}
     */
    public void endNode() {
        Status status = (Status)statusStack.peek();
        if (status == Status.END) {
            statusStack.popSilently();
            Class type = (Class)typeStack.pop();
            status = (Status)statusStack.peek();
            if (isArray(type)) {
                if ((mode & EXPLICIT_MODE) != 0) {
                    endObject();
                }
                if (status == Status.ELEMENT || status == Status.ARRAY) {
                    endArray();
                    statusStack.popSilently();
                    status = (Status)statusStack.peek();
                } else {
                    throw new IllegalStateException("Cannot end array");
                }
                if (status == Status.VIRTUAL) {
                    statusStack.popSilently();
                    endObject();
                    status = (Status)statusStack.peek();
                }
            } else if (status != Status.OBJECT && status != Status.PROPERTY) {
                throw new IllegalStateException("Cannot end object");
            } else {
                endObject();
            }
        } else if (status == Status.VALUE || status == Status.VIRTUAL) {
            statusStack.popSilently();
            final Class type = (Class)typeStack.pop();
            if (status == Status.VIRTUAL && (mode & EXPLICIT_MODE) != 0) {
                nextElement();
                addLabel("$");
            }
            if (isArray(type)) {
                startArray();
                endArray();
            } else if ((mode & EXPLICIT_MODE) != 0 || status == Status.VALUE) {
                Type jsonType = getType(type);
                if (jsonType != Type.NULL) {
                    startObject(null);
                    endObject();
                } else {
                    addValue("null", jsonType);
                }
            }
            if (status == Status.VIRTUAL) {
                endObject();
            }
            status = (Status)statusStack.peek();
        }
        if (status == Status.PROPERTY || status == Status.OBJECT) {
            if (typeStack.size() == 0) {
                status = (Status)statusStack.pop();
                if (status != Status.OBJECT) {
                    throw new IllegalStateException("Cannot end object");
                }
                if ((mode & DROP_ROOT_MODE) == 0) {
                    endObject();
                }
            } else {
                statusStack.replaceSilently(Status.END);
            }
        } else {
            throw new IllegalStateException("Cannot end object");
        }
    }

    private Type getType(Class clazz) {
        return (clazz == Mapper.Null.class || clazz == null)
            ? Type.NULL
            : (clazz == Boolean.class || clazz == Boolean.TYPE) ? Type.BOOLEAN : NUMBER_TYPES
                .contains(clazz) ? Type.NUMBER : Type.STRING;
    }

    private boolean isArray(Class clazz) {
        return clazz.isArray()
            || Collection.class.isAssignableFrom(clazz)
            || Map.class.isAssignableFrom(clazz)
            || (((mode & EXPLICIT_MODE) == 0) && Map.Entry.class.isAssignableFrom(clazz));
    }

    /**
     * Start a JSON object.
     * 
     * @param name the object's name (may be <code>null</code> for an empty object)
     * @since upcoming
     */
    protected abstract void startObject(String name);

    /**
     * Add a label to a JSON object.
     * 
     * @param name the label's name
     * @since upcoming
     */
    protected abstract void addLabel(String name);

    /**
     * Add a value to a JSON object's label or to an array.
     * 
     * @param value the value itself
     * @param type the JSON type
     * @since upcoming
     */
    protected abstract void addValue(String value, Type type);

    /**
     * Start a JSON array.
     * 
     * @since upcoming
     */
    protected abstract void startArray();

    /**
     * Prepare a JSON object or array for another element.
     * 
     * @since upcoming
     */
    protected abstract void nextElement();

    /**
     * End the JSON array.
     * 
     * @since upcoming
     */
    protected abstract void endArray();

    /**
     * End the JSON object.
     * 
     * @since upcoming
     */
    protected abstract void endObject();
}
