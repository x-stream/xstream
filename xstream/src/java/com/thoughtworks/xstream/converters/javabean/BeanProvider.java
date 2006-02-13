package com.thoughtworks.xstream.converters.javabean;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Iterator;

/**
 * Pure Java ObjectFactory that instantiates objects using standard Java
 * reflection, however the types of objects that can be constructed are limited.
 * <p/>Can newInstance: classes with public visibility, outer classes, static
 * inner classes, classes with default constructors and any class that
 * implements java.io.Serializable. Cannot newInstance: classes without public
 * visibility, non-static inner classes, classes without default constructors.
 * Note that any code in the constructor of a class will be executed when the
 * ObjectFactory instantiates the object.
 * </p>
 */
public class BeanProvider {

//    private final Map serializedDataCache = Collections.synchronizedMap(new HashMap());
//
    protected PropertyDictionary propertyDictionary = new PropertyDictionary();
    
    protected static final Object[] NO_PARAMS = new Object[0];

    public Object newInstance(Class type) {
        try {
            return getDefaultConstrutor(type).newInstance(NO_PARAMS);
        } catch (InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            } else if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            } else {
                throw new ObjectAccessException("Constructor for " + type.getName()
                        + " threw an exception", e);
            }
        }
    }

//    private Object instantiateUsingSerialization(Class type) {
//        try {
//            byte[] data;
//            if (serializedDataCache.containsKey(type)) {
//                data = (byte[]) serializedDataCache.get(type);
//            } else {
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                DataOutputStream stream = new DataOutputStream(bytes);
//                stream.writeShort(ObjectStreamConstants.STREAM_MAGIC);
//                stream.writeShort(ObjectStreamConstants.STREAM_VERSION);
//                stream.writeByte(ObjectStreamConstants.TC_OBJECT);
//                stream.writeByte(ObjectStreamConstants.TC_CLASSDESC);
//                stream.writeUTF(type.getName());
//                stream.writeLong(ObjectStreamClass.lookup(type).getSerialVersionUID());
//                stream.writeByte(2); // classDescFlags (2 = Serializable)
//                stream.writeShort(0); // field count
//                stream.writeByte(ObjectStreamConstants.TC_ENDBLOCKDATA);
//                stream.writeByte(ObjectStreamConstants.TC_NULL);
//                data = bytes.toByteArray();
//                serializedDataCache.put(type, data);
//            }
//
//            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
//            return in.readObject();
//        } catch (IOException e) {
//            throw new ObjectAccessException("", e);
//        } catch (ClassNotFoundException e) {
//            throw new ObjectAccessException("", e);
//        }
//    }

    public void visitSerializableProperties(Object object, Visitor visitor) {
        for (Iterator iterator = propertyDictionary.serializablePropertiesFor(object.getClass()); iterator
                .hasNext();) {
            BeanProperty property = (BeanProperty) iterator.next();
            try {
                Object value = property.get(object);
                visitor.visit(property.getName(), property.getType(), value);
            } catch (IllegalArgumentException e) {
                throw new ObjectAccessException("Could not get property " + property.getClass()
                        + "." + property.getName(), e);
            } catch (IllegalAccessException e) {
                throw new ObjectAccessException("Could not get property " + property.getClass()
                        + "." + property.getName(), e);
            }
        }
    }

    public void writeProperty(Object object, String propertyName, Object value) {
        BeanProperty property = propertyDictionary.property(object.getClass(), propertyName);
        try {
            property.set(object, value);
        } catch (IllegalArgumentException e) {
            throw new ObjectAccessException("Could not set property " + object.getClass() + "."
                    + property.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Could not set property " + object.getClass() + "."
                    + property.getName(), e);
        }
    }

    public Class getPropertyType(Object object, String name) {
        return propertyDictionary.property(object.getClass(), name).getType();
    }

    public boolean propertyDefinedInClass(String name, Class type) {
        return propertyDictionary.property(type, name) != null;
    }

    /**
     * Returns true if the Bean provider can instantiate the specified class
     */
    public boolean canInstantiate(Class type) {
        return getDefaultConstrutor(type) != null;
    }
    
    /**
     * Returns the default constructor, or null if none is found
     * @param type
     */
    protected Constructor getDefaultConstrutor(Class type) {
        Constructor[] constructors = type.getConstructors();
        for (int i = 0; i < constructors.length; i++) {
            Constructor c = constructors[i];
            if (c.getParameterTypes().length == 0 && Modifier.isPublic(c.getModifiers()))
                return c;
        }
        return null;
    }
    
    interface Visitor {
        void visit(String name, Class type, Object value);
    }

}