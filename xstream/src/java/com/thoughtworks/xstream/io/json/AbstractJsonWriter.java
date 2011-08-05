/*
 * Copyright (C) 2009, 2010, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 20. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.json;

import java.io.Externalizable;
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
 * Note, that XStream's implicit collection feature is only compatible with the syntax in
 * {@link #EXPLICIT_MODE}.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
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
     * XStream is normally using attributes in XML that have no real equivalent in JSON.
     * Additionally it is essential in XML that the individual child elements of a tag keep
     * order and may have the same tag name. XStream's model relies on both characteristics.
     * However, properties of a JSON object do not have a defined order, but their names have to
     * be unique. Only a JSON array defines the order of its elements.
     * </p>
     * <p>
     * Therefore XStream uses in explicit mode a JSON format that supports the original
     * requirements at the expense of the simplicity of the JSON objects and arrays. Each Java
     * object will be represented by a JSON object with a single property representing the name
     * of the object and an array as value that contains two more arrays. The first one contains
     * a JSON object with all attributes, the second one the value of the Java object which can
     * be null, a string or integer value or again a new JSON object representing a Java object.
     * Here an example of an string array with one member, where the array and the string has an
     * additional attribute 'id':
     * 
     * <pre>
     * {&quot;string-array&quot;:[[{&quot;id&quot;:&quot;1&quot;}],[{&quot;string&quot;:[[{&quot;id&quot;:&quot;2&quot;}],[&quot;Joe&quot;]]}]]}
     * </pre>
     * 
     * This format can be used to always deserialize into Java again.
     * </p>
     * <p>
     * This mode cannot be combined with one of the other modes.
     * </p>
     * 
     * @since 1.4
     */
    public static final int EXPLICIT_MODE = 4;

    public static class Type {
        public static Type NULL = new Type();
        public static Type STRING = new Type();
        public static Type NUMBER = new Type();
        public static Type BOOLEAN = new Type();
    }
    
    private static class StackElement {
        final Class type;
        int status;
        public StackElement(Class type, int status) {
            this.type = type;
            this.status = status;
        }
    }
    
    private static class IllegalWriterStateException extends IllegalStateException {
        public IllegalWriterStateException(int from, int to, String element) {
            super("Cannot turn from state " + getState(from) + " into state " + getState(to)
                + (element ==  null ? "" : " for property " + element));
        }
        private static String getState(int state) {
            switch (state) {
            case STATE_ROOT: return "ROOT";
            case STATE_END_OBJECT: return "END_OBJECT";
            case STATE_START_OBJECT: return "START_OBJECT";
            case STATE_START_ATTRIBUTES: return "START_ATTRIBUTES";
            case STATE_NEXT_ATTRIBUTE: return "NEXT_ATTRIBUTE";
            case STATE_END_ATTRIBUTES: return "END_ATTRIBUTES";
            case STATE_START_ELEMENTS: return "START_ELEMENTS";
            case STATE_NEXT_ELEMENT: return "NEXT_ELEMENT";
            case STATE_END_ELEMENTS: return "END_ELEMENTS";
            case STATE_SET_VALUE: return "SET_VALUE";
            default: throw new IllegalArgumentException("Unknown state provided: " + state
                + ", cannot create message for IllegalWriterStateException");
            }
        }
    }

    private static final int STATE_ROOT = 1 << 0;
    private static final int STATE_END_OBJECT = 1 << 1;
    private static final int STATE_START_OBJECT = 1 << 2;
    private static final int STATE_START_ATTRIBUTES = 1 << 3;
    private static final int STATE_NEXT_ATTRIBUTE = 1 << 4;
    private static final int STATE_END_ATTRIBUTES = 1 << 5;
    private static final int STATE_START_ELEMENTS = 1 << 6;
    private static final int STATE_NEXT_ELEMENT = 1 << 7;
    private static final int STATE_END_ELEMENTS = 1 << 8;
    private static final int STATE_SET_VALUE = 1 << 9;

    private static final List NUMBER_TYPES = Arrays.asList(new Class[]{
        byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class,
        Long.class, float.class, Float.class, double.class, Double.class});
    private int mode;
    private FastStack stack = new FastStack(16);
    private int expectedStates;

    /**
     * Construct a JSON writer.
     * 
     * @since 1.4
     */
    public AbstractJsonWriter() {
        this(new NoNameCoder());
    }

    /**
     * Construct a JSON writer with a special mode.
     * 
     * @param mode a bit mask of the mode constants
     * @since 1.4
     */
    public AbstractJsonWriter(int mode) {
        this(mode, new NoNameCoder());
    }

    /**
     * Construct a JSON writer with a special name coder.
     * 
     * @param nameCoder the name coder to use
     * @since 1.4
     */
    public AbstractJsonWriter(NameCoder nameCoder) {
        this(0, nameCoder);
    }

    /**
     * Construct a JSON writer with a special mode and name coder.
     * 
     * @param mode a bit mask of the mode constants
     * @param nameCoder the name coder to use
     * @since 1.4
     */
    public AbstractJsonWriter(int mode, NameCoder nameCoder) {
        super(nameCoder);
        this.mode = (mode & EXPLICIT_MODE) > 0 ? EXPLICIT_MODE : mode;
        stack.push(new StackElement(null, STATE_ROOT));
        expectedStates = STATE_START_OBJECT;
    }

    public void startNode(String name, Class clazz) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        stack.push(new StackElement(clazz, ((StackElement)stack.peek()).status));
        handleCheckedStateTransition(STATE_START_OBJECT, name, null);
        expectedStates = STATE_SET_VALUE | STATE_NEXT_ATTRIBUTE | STATE_START_OBJECT | STATE_NEXT_ELEMENT | STATE_ROOT;
    }

    public void startNode(String name) {
        startNode(name, null);
    }

    public void addAttribute(String name, String value) {
        handleCheckedStateTransition(STATE_NEXT_ATTRIBUTE, name, value);
        expectedStates = STATE_SET_VALUE | STATE_NEXT_ATTRIBUTE | STATE_START_OBJECT | STATE_NEXT_ELEMENT | STATE_ROOT;
    }

    public void setValue(String text) {
        Class type = ((StackElement)stack.peek()).type;
        if ((type == Character.class || type == Character.TYPE) && "".equals(text)) {
            text = "\u0000";
        }
        handleCheckedStateTransition(STATE_SET_VALUE, null, text);
        expectedStates = STATE_NEXT_ELEMENT | STATE_ROOT;
    }

    public void endNode() {
        int size = stack.size();
        int nextState = size > 2 ? STATE_NEXT_ELEMENT : STATE_ROOT;
        handleCheckedStateTransition(nextState, null, null);
        stack.pop();
        ((StackElement)stack.peek()).status = nextState;
        expectedStates = STATE_START_OBJECT;
        if (size > 2) { 
            expectedStates |= STATE_NEXT_ELEMENT | STATE_ROOT;
        }
    }

    private void handleCheckedStateTransition(final int requiredState, final String elementToAdd, final String valueToAdd)
    {
        final StackElement stackElement = (StackElement)stack.peek();
        if ((expectedStates & requiredState) == 0) {
            throw new IllegalWriterStateException(stackElement.status, requiredState, elementToAdd);
        }
        int currentState = handleStateTransition(stackElement.status, requiredState, elementToAdd, valueToAdd);
        stackElement.status = currentState;
    }

    private int handleStateTransition(int currentState, final int requiredState, final String elementToAdd, final String valueToAdd)
    {
        int size = stack.size();
        Class currentType = ((StackElement)stack.peek()).type;
        boolean isArray = size > 1 && isArray(currentType); 
        boolean isArrayElement = size > 1 && isArray(((StackElement)stack.get(size-2)).type); 
        switch(currentState) {
        case STATE_ROOT:
            if (requiredState == STATE_START_OBJECT) {
                currentState = handleStateTransition(STATE_START_ELEMENTS, STATE_START_OBJECT, elementToAdd, null);
                return requiredState;
            }
            throw new IllegalWriterStateException(currentState, requiredState, elementToAdd);
            
        case STATE_END_OBJECT:
            switch(requiredState) {
            case STATE_START_OBJECT:
                currentState = handleStateTransition(currentState, STATE_NEXT_ELEMENT, null, null);
                currentState = handleStateTransition(currentState, STATE_START_OBJECT, elementToAdd, null);
                return requiredState;
            case STATE_NEXT_ELEMENT:
                nextElement();
                return requiredState;
            case STATE_ROOT:
                if (((mode & DROP_ROOT_MODE) == 0 || size > 2) && (mode & EXPLICIT_MODE) == 0) {
                    endObject();
                }
                return requiredState;
            default:
                throw new IllegalWriterStateException(currentState, requiredState, elementToAdd);
            }
            
        case STATE_START_OBJECT:
            switch(requiredState) {
            case STATE_SET_VALUE:
            case STATE_START_OBJECT:
            case STATE_NEXT_ELEMENT:
                if (!isArrayElement || (mode & EXPLICIT_MODE) != 0) {
                    currentState = handleStateTransition(currentState, STATE_START_ATTRIBUTES, null, null);
                    currentState = handleStateTransition(currentState, STATE_END_ATTRIBUTES, null, null);
                }
                currentState = STATE_START_ELEMENTS;
                
                switch(requiredState) {
                case STATE_SET_VALUE:
                    currentState = handleStateTransition(currentState, STATE_SET_VALUE, null, valueToAdd);
                    break;
                case STATE_START_OBJECT:
                    currentState = handleStateTransition(currentState, STATE_START_OBJECT, elementToAdd, null);
                    break;
                case STATE_NEXT_ELEMENT:
                    currentState = handleStateTransition(currentState, STATE_SET_VALUE, null, null);
                    currentState = handleStateTransition(currentState, STATE_NEXT_ELEMENT, null, null);
                    break;
                }
                return requiredState;
            case STATE_START_ATTRIBUTES:
                if ((mode & EXPLICIT_MODE) != 0) {
                    startArray();
                }
                return requiredState;
            case STATE_NEXT_ATTRIBUTE:
                if ((mode & EXPLICIT_MODE) != 0 || !isArray) {
                    currentState = handleStateTransition(currentState, STATE_START_ATTRIBUTES, null, null);
                    currentState = handleStateTransition(currentState, STATE_NEXT_ATTRIBUTE, elementToAdd, valueToAdd);
                    return requiredState;
                } else {
                    return STATE_START_OBJECT;
                }
            default:
                throw new IllegalWriterStateException(currentState, requiredState, elementToAdd);
            }
            
        case STATE_NEXT_ELEMENT:
            switch(requiredState) {
            case STATE_START_OBJECT:
                nextElement();
                if (!isArrayElement && (mode & EXPLICIT_MODE) == 0) {
                    addLabel(elementToAdd);
                    if ((mode & EXPLICIT_MODE) == 0 && isArray) {
                        startArray();
                    }
                    return requiredState;
                }
                break;
            case STATE_ROOT:
                currentState = handleStateTransition(currentState, STATE_END_OBJECT, null, null);
                currentState = handleStateTransition(currentState, STATE_ROOT, null, null);
                return requiredState;
            case STATE_NEXT_ELEMENT:
            case STATE_END_OBJECT:
                currentState = handleStateTransition(currentState, STATE_END_ELEMENTS, null, null);
                currentState = handleStateTransition(currentState, STATE_END_OBJECT, null, null);
                if ((mode & EXPLICIT_MODE) == 0 && !isArray) {
                    endObject();
                }
                return requiredState;
            case STATE_END_ELEMENTS:
                if ((mode & EXPLICIT_MODE) == 0 && isArray) {
                    endArray();
                }
                return requiredState;
            default:
                throw new IllegalWriterStateException(currentState, requiredState, elementToAdd);
            }
            // fall through
        case STATE_START_ELEMENTS:
            switch(requiredState) {
            case STATE_START_OBJECT:
                if ((mode & DROP_ROOT_MODE) == 0 || size > 2) {
                    if (!isArrayElement || (mode & EXPLICIT_MODE) != 0) {
                        if (!"".equals(valueToAdd)) {
                            startObject();
                        }
                        addLabel(elementToAdd);
                    }
                    if ((mode & EXPLICIT_MODE) != 0) {
                        startArray();
                    }
                }
                if ((mode & EXPLICIT_MODE) == 0) {
                    if (isArray) {
                        startArray();
                    }
                }
                return requiredState;
            case STATE_SET_VALUE:
                if ((mode & STRICT_MODE) != 0 && size == 2) {
                    throw new ConversionException("Single value cannot be root element");
                }
                if (valueToAdd == null) {
                    if (currentType == Mapper.Null.class) {
                       addValue("null", Type.NULL);
                    } else if ((mode & EXPLICIT_MODE) == 0 && !isArray) {
                        startObject();
                        endObject();
                    }
                } else {
                    addValue(valueToAdd, getType(currentType));
                }
                return requiredState;
            case STATE_END_ELEMENTS:
            case STATE_NEXT_ELEMENT:
                if ((mode & EXPLICIT_MODE) == 0) {
                    if (isArray) {
                        endArray();
                    } else {
                        endObject();
                    }
                }
                return requiredState;
            default:
                throw new IllegalWriterStateException(currentState, requiredState, elementToAdd);
            }

        case STATE_END_ELEMENTS:
            switch(requiredState) {
            case STATE_END_OBJECT:
                if ((mode & EXPLICIT_MODE) != 0) {
                    endArray();
                    endArray();
                    endObject();
                }
                return requiredState;
            default:
                throw new IllegalWriterStateException(currentState, requiredState, elementToAdd);
            }

        case STATE_START_ATTRIBUTES:
            switch(requiredState) {
            case STATE_NEXT_ATTRIBUTE:
                if (elementToAdd != null) {
                    String name = ((mode & EXPLICIT_MODE) == 0 ? "@" : "" ) + elementToAdd;
                    startObject();
                    addLabel(name);
                    addValue(valueToAdd, Type.STRING);
                }
                return requiredState;
            }
            // fall through
        case STATE_NEXT_ATTRIBUTE:
            switch(requiredState) {
            case STATE_END_ATTRIBUTES:
                if ((mode & EXPLICIT_MODE) != 0) {
                    if (currentState == STATE_NEXT_ATTRIBUTE) {
                        endObject();
                    }
                    endArray();
                    nextElement();
                    startArray();
                }
                return requiredState;
            case STATE_NEXT_ATTRIBUTE:
                if (!isArray || (mode & EXPLICIT_MODE) != 0) {
                    nextElement();
                    String name = ((mode & EXPLICIT_MODE) == 0 ? "@" : "" ) + elementToAdd;
                    addLabel(name);
                    addValue(valueToAdd, Type.STRING);
                }
                return requiredState;
            case STATE_SET_VALUE:
            case STATE_START_OBJECT:
                currentState = handleStateTransition(currentState, STATE_END_ATTRIBUTES, null, null);
                currentState = handleStateTransition(currentState, STATE_START_ELEMENTS, null, null);
                switch (requiredState) {
                case STATE_SET_VALUE:
                    if ((mode & EXPLICIT_MODE) == 0) {
                        addLabel("$");
                    }
                    currentState = handleStateTransition(currentState, STATE_SET_VALUE, null, valueToAdd);
                    if ((mode & EXPLICIT_MODE) == 0) {
                        endObject();
                    }
                    break;
                case STATE_START_OBJECT:
                    currentState = handleStateTransition(currentState, STATE_START_OBJECT, elementToAdd, (mode & EXPLICIT_MODE) == 0 ? "" : null);
                    break;
                case STATE_END_OBJECT:
                    currentState = handleStateTransition(currentState, STATE_SET_VALUE, null, null);
                    currentState = handleStateTransition(currentState, STATE_END_OBJECT, null, null);
                    break;
                }
                return requiredState;
            case STATE_NEXT_ELEMENT:
                currentState = handleStateTransition(currentState, STATE_END_ATTRIBUTES, null, null);
                currentState = handleStateTransition(currentState, STATE_END_OBJECT, null, null);
                return requiredState;
            case STATE_ROOT:
                currentState = handleStateTransition(currentState, STATE_END_ATTRIBUTES, null, null);
                currentState = handleStateTransition(currentState, STATE_END_OBJECT, null, null);
                currentState = handleStateTransition(currentState, STATE_ROOT, null, null);
                return requiredState;
            default:
                throw new IllegalWriterStateException(currentState, requiredState, elementToAdd);
            }

        case STATE_END_ATTRIBUTES:
            switch(requiredState) {
            case STATE_START_ELEMENTS:
                if ((mode & EXPLICIT_MODE) == 0) {
                    nextElement();
                }
                break;
            case STATE_END_OBJECT:
                currentState = handleStateTransition(STATE_START_ELEMENTS, STATE_END_ELEMENTS, null, null);
                currentState = handleStateTransition(currentState, STATE_END_OBJECT, null, null);
                break;
            default:
                throw new IllegalWriterStateException(currentState, requiredState, elementToAdd);
            }
            return requiredState;
            
        case STATE_SET_VALUE:
            switch(requiredState) {
            case STATE_END_ELEMENTS:
                if ((mode & EXPLICIT_MODE) == 0 && isArray) {
                    endArray();
                }
                return requiredState;
            case STATE_NEXT_ELEMENT:
                currentState = handleStateTransition(currentState, STATE_END_ELEMENTS, null, null);
                currentState = handleStateTransition(currentState, STATE_END_OBJECT, null, null);
                return requiredState;
            case STATE_ROOT:
                currentState = handleStateTransition(currentState, STATE_END_ELEMENTS, null, null);
                currentState = handleStateTransition(currentState, STATE_END_OBJECT, null, null);
                currentState = handleStateTransition(currentState, STATE_ROOT, null, null);
                return requiredState;
            default:
                throw new IllegalWriterStateException(currentState, requiredState, elementToAdd);
            }
        }

        throw new IllegalWriterStateException(currentState, requiredState, elementToAdd);
    }

    private Type getType(Class clazz) {
        return (clazz == Mapper.Null.class || clazz == null)
            ? Type.NULL
            : (clazz == Boolean.class || clazz == Boolean.TYPE) 
                ? Type.BOOLEAN 
                : NUMBER_TYPES.contains(clazz) 
                    ? Type.NUMBER 
                    : Type.STRING;
    }

    /**
     * Method to declare various Java types to be handles as JSON array.
     * 
     * @param clazz the type
     * @return <code>true</code> if handles as array
     * @since 1.4
     */
    protected boolean isArray(Class clazz) {
        return clazz != null && (clazz.isArray()
            || Collection.class.isAssignableFrom(clazz)
            || Externalizable.class.isAssignableFrom(clazz)
            || Map.class.isAssignableFrom(clazz)
            || Map.Entry.class.isAssignableFrom(clazz));
    }

    /**
     * Start a JSON object.
     * 
     * @since 1.4
     */
    protected abstract void startObject();

    /**
     * Add a label to a JSON object.
     * 
     * @param name the label's name
     * @since 1.4
     */
    protected abstract void addLabel(String name);

    /**
     * Add a value to a JSON object's label or to an array.
     * 
     * @param value the value itself
     * @param type the JSON type
     * @since 1.4
     */
    protected abstract void addValue(String value, Type type);

    /**
     * Start a JSON array.
     * 
     * @since 1.4
     */
    protected abstract void startArray();

    /**
     * Prepare a JSON object or array for another element.
     * 
     * @since 1.4
     */
    protected abstract void nextElement();

    /**
     * End the JSON array.
     * 
     * @since 1.4
     */
    protected abstract void endArray();

    /**
     * End the JSON object.
     * 
     * @since 1.4
     */
    protected abstract void endObject();
}
