package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.CustomObjectInputStream;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.Externalizable;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInput;
import java.io.ObjectInputValidation;
import java.io.ObjectOutput;
import java.util.Map;

/**
 * Converts any object that implements the java.io.Externalizable interface, allowing compatability with native Java
 * serialization.
 *
 * @author Joe Walnes
 */
public class ExternalizableConverter implements Converter {

    private ClassMapper classMapper;

    public ExternalizableConverter(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    public boolean canConvert(Class type) {
        return Externalizable.class.isAssignableFrom(type);
    }

    public void marshal(Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        try {
            Externalizable externalizable = (Externalizable) source;
            CustomObjectOutputStream.StreamCallback callback = new CustomObjectOutputStream.StreamCallback() {
                public void writeToStream(Object object) {
                    if (object == null) {
                        writer.startNode("null");
                        writer.endNode();
                    } else {
                        writer.startNode(classMapper.lookupName(object.getClass()));
                        context.convertAnother(object);
                        writer.endNode();
                    }
                }

                public void writeFieldsToStream(Map fields) {
                    throw new UnsupportedOperationException();
                }

                public void defaultWriteObject() {
                    throw new UnsupportedOperationException();
                }

                public void close() {
                    throw new UnsupportedOperationException("Objects are not allowed to call ObjecOutput.close() from writeExternal()");
                }
            };
            ObjectOutput objectOutput = CustomObjectOutputStream.getInstance(context, callback);
            externalizable.writeExternal(objectOutput);
        } catch (IOException e) {
            throw new ConversionException("Cannot serialize " + source.getClass().getName() + " using Externalization", e);
        }
    }

    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Class type = classMapper.lookupType(reader.getNodeName());
        try {
            Externalizable externalizable = (Externalizable) type.newInstance();
            CustomObjectInputStream.StreamCallback callback = new CustomObjectInputStream.StreamCallback() {
                public Object readFromStream() {
                    reader.moveDown();
                    Object streamItem = context.convertAnother(type, classMapper.lookupType(reader.getNodeName()));
                    reader.moveUp();
                    return streamItem;
                }

                public Map readFieldsFromStream() {
                    throw new UnsupportedOperationException();
                }

                public void defaultReadObject() {
                    throw new UnsupportedOperationException();
                }

                public void registerValidation(ObjectInputValidation validation, int priority) throws NotActiveException {
                    throw new NotActiveException("stream inactive");
                }

                public void close() {
                    throw new UnsupportedOperationException("Objects are not allowed to call ObjectInput.close() from readExternal()");
                }
            };
            ObjectInput objectInput = CustomObjectInputStream.getInstance(context, callback);
            externalizable.readExternal(objectInput);
            return externalizable;
        } catch (InstantiationException e) {
            throw new ConversionException("Cannot construct " + type.getClass(), e);
        } catch (IllegalAccessException e) {
            throw new ConversionException("Cannot construct " + type.getClass(), e);
        } catch (IOException e) {
            throw new ConversionException("Cannot externalize " + type.getClass(), e);
        } catch (ClassNotFoundException e) {
            throw new ConversionException("Cannot externalize " + type.getClass(), e);
        }
    }
}
