//020 8330 0577 bob
package com.thoughtworks.xstream.objecttree.reflection;

import com.thoughtworks.xstream.objecttree.ObjectAccessException;
import com.thoughtworks.xstream.objecttree.ObjectTree;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class ReflectionObjectGraph implements ObjectTree {

    private LinkedList fieldStack = new LinkedList();
    private LinkedList instanceStack = new LinkedList();
    private Class rootType;
    private ObjectFactory objectFactory;

    public ReflectionObjectGraph(Object root, ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
        init(root);
    }

    public ReflectionObjectGraph(Class rootType, ObjectFactory objectFactory) {
        this.rootType = rootType;
        this.objectFactory = objectFactory;
        init(null);
    }

    private static class RootHolder {
        Object value;
    }

    private void init(Object root) {
        RootHolder holder = new RootHolder();
        holder.value = root;
        instanceStack.addLast(holder);
        push("value");
    }

    public void push(String fieldName) {
        Object top = instanceStack.getLast();

        Field field = null;
        Class currentClass = top.getClass();
        try {

            while (field == null) {
                try {
                    field = currentClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    if (Object.class.equals(currentClass)) {
                        throw new ObjectAccessException("Cannot access field " + fieldName, e);
                    }
                    currentClass = currentClass.getSuperclass();
                }
            }

        } catch (SecurityException e) {
            throw new ObjectAccessException("Cannot access field " + fieldName, e);
        }
        field.setAccessible(true);
        fieldStack.addLast(field);

        try {
            instanceStack.addLast(field.get(top));
        } catch (IllegalArgumentException e) {
            throw new ObjectAccessException("Cannot access field " + fieldName, e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot access field " + fieldName, e);
        }

    }

    public void pop() {
        fieldStack.removeLast();
        instanceStack.removeLast();
    }

    public Class type() {
        if (fieldStack.size() == 1) {
            return rootType;
        } else {
            Field field = (Field) fieldStack.getLast();
            Class type = field.getType();
            return type;
        }
    }

    public Object get() {
        return instanceStack.getLast();
    }

    public void set(Object value) {
        try {
            instanceStack.removeLast();
            Field field = (Field) fieldStack.getLast();
            Object top = instanceStack.getLast();
            field.set(top, value);
            instanceStack.addLast(value);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot set field", e);
        }
    }

    public void create(Class type) {
        set(objectFactory.create(type));
    }

    public String[] fieldNames() {
        List fieldNames = new LinkedList();
        Class theClass = get().getClass();
        Class currentClass = theClass;

        while (!Object.class.equals(currentClass)) {
            getFields(fieldNames, currentClass);
            currentClass = currentClass.getSuperclass();
        }

        String[] result = new String[fieldNames.size()];
        fieldNames.toArray(result);
        return result;
    }

    private void getFields(List fieldNames, Class theClass) {
        Field[] fields = theClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (!field.getName().startsWith("this$")) {
                fieldNames.add(field.getName());
            }
        }
    }

    public ObjectTree newStack(Class type) {
        return new ReflectionObjectGraph(type, objectFactory);
    }

    public ObjectTree newStack(Object instance) {
        return new ReflectionObjectGraph(instance, objectFactory);
    }

}
