package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts a java.lang.reflect.Method to XML.
 *
 * @author Aslak Helles&oslash;y
 */
public class JavaMethodConverter implements Converter {

    private final ClassLoader classLoader;

    /**
     * @deprecated As of 1.2 - use other constructor and explicitly supply a ClassLoader.
     */
    public JavaMethodConverter() {
        this(JavaMethodConverter.class.getClassLoader());
    }

    public JavaMethodConverter(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean canConvert(Class type) {
        return type.equals(Method.class) || type.equals(Constructor.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source instanceof Method) {
            Method method = (Method) source;
            String declaringClassName = method.getDeclaringClass().getName();
            marshalMethod(writer, declaringClassName, method.getName(), method.getParameterTypes());
        } else {
            Constructor method = (Constructor) source;
            String declaringClassName = method.getDeclaringClass().getName();
            marshalMethod(writer, declaringClassName, null, method.getParameterTypes());
        }
    }

    private void marshalMethod(HierarchicalStreamWriter writer, String declaringClassName, String methodName, Class[] parameterTypes) {

        writer.startNode("class");
        writer.setValue(declaringClassName);
        writer.endNode();

        if (methodName != null) {
            // it's a method and not a ctor
            writer.startNode("name");
            writer.setValue(methodName);
            writer.endNode();
        }

        writer.startNode("parameter-types");
        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameterType = parameterTypes[i];
            writer.startNode("class");
            writer.setValue(parameterType.getName());
            writer.endNode();
        }
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        try {
            boolean isMethodNotConstructor = context.getRequiredType().equals(Method.class);

            reader.moveDown();
            String declaringClassName = reader.getValue();
            Class declaringClass = loadClass(declaringClassName);
            reader.moveUp();

            String methodName = null;
            if (isMethodNotConstructor) {
                reader.moveDown();
                methodName = reader.getValue();
                reader.moveUp();
            }

            reader.moveDown();
            List parameterTypeList = new ArrayList();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String parameterTypeName = reader.getValue();
                parameterTypeList.add(loadClass(parameterTypeName));
                reader.moveUp();
            }
            Class[] parameterTypes = (Class[]) parameterTypeList.toArray(new Class[parameterTypeList.size()]);
            reader.moveUp();

            if (isMethodNotConstructor) {
                return declaringClass.getDeclaredMethod(methodName, parameterTypes);
            } else {
                return declaringClass.getDeclaredConstructor(parameterTypes);
            }
        } catch (ClassNotFoundException e) {
            throw new ConversionException(e);
        } catch (NoSuchMethodException e) {
            throw new ConversionException(e);
        }
    }

    private Class loadClass(String className) throws ClassNotFoundException {
        Class primitiveClass = primitiveClassForName(className);
        if( primitiveClass != null ){
            return primitiveClass;
        }
        return classLoader.loadClass(className);
    }

    /**
     * Lookup table for primitive types.
     */
    private Class primitiveClassForName(String name) {
        return  name.equals("void") ? Void.TYPE :
                name.equals("boolean") ? Boolean.TYPE :
                name.equals("byte") ? Byte.TYPE :
                name.equals("char") ? Character.TYPE :
                name.equals("short") ? Short.TYPE :
                name.equals("int") ? Integer.TYPE :
                name.equals("long") ? Long.TYPE :
                name.equals("float") ? Float.TYPE :
                name.equals("double") ? Double.TYPE :
                null;
    }
}
