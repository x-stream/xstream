package com.thoughtworks.xstream.objecttree.reflection;

import com.thoughtworks.someobjects.FunnyConstructor;
import com.thoughtworks.someobjects.X;
import com.thoughtworks.someobjects.Y;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import junit.framework.TestCase;

import java.util.Arrays;

public class ReflectionObjectGraphTest extends TestCase {

    private ObjectFactory objectFactory = new SunReflectionObjectFactory();

    public void testGraphStartsOffEmpty() {
        ObjectTree writer = new ReflectionObjectGraph(X.class, objectFactory);
        assertEquals(X.class, writer.type());
        assertNull(writer.get());
    }

    public void testRootItemCanBeCreated() {
        ObjectTree writer = new ReflectionObjectGraph(X.class, objectFactory);
        writer.create(X.class);
        assertEquals(X.class, writer.type());
        assertEquals(X.class, writer.get().getClass());
    }

    class X2 extends X {
    }

    public void testRootItemCanBeCreatedWithCustomType() {
        ObjectTree writer = new ReflectionObjectGraph(X.class, objectFactory);
        writer.create(X2.class);
        assertEquals(X.class, writer.type());
        assertEquals(X2.class, writer.get().getClass());
    }


    public void testCreateSingleObjectAndSetSimpleFields() {
        X expected = new X();
        expected.aStr = "hello";
        expected.anInt = 22;

        ObjectTree writer = new ReflectionObjectGraph(X.class, objectFactory);
        writer.create(X.class);

        writer.push("aStr");
        assertEquals(String.class, writer.type());
        writer.set("hello");
        assertEquals(String.class, writer.type());
        writer.pop();

        writer.push("anInt");
        assertEquals(int.class, writer.type());
        writer.set(new Integer(22));
        assertEquals(int.class, writer.type());
        writer.pop();

        assertEquals(expected, writer.get());
    }

    public void testCreateObjectInsideAnotherObject() {
        X expected = new X();
        expected.aStr = "hello";
        expected.innerObj = new Y();
        expected.innerObj.yField = "world";
        expected.anInt = 3;

        ObjectTree writer = new ReflectionObjectGraph(X.class, objectFactory);
        writer.create(X.class);

        writer.push("aStr");
        writer.set("hello");
        writer.pop();

        writer.push("innerObj");
        writer.create(Y.class);
        writer.push("yField");
        writer.set("world");
        writer.pop();
        writer.pop();

        writer.push("anInt");
        writer.set(new Integer(3));
        writer.pop();

        assertEquals(expected, writer.get());
    }

    public void testObjectCanBeInstantiatedWithoutDefaultConstructor() {
        FunnyConstructor expected = new FunnyConstructor(33);

        ObjectTree writer = new ReflectionObjectGraph(FunnyConstructor.class, objectFactory);
        writer.create(FunnyConstructor.class);

        writer.push("i");
        writer.set(new Integer(33));
        writer.pop();

        assertEquals(expected, writer.get());
    }

    public void testIteratesOverFieldsOfCurrentObject() {
        ObjectTree writer = new ReflectionObjectGraph(X.class, objectFactory);
        writer.create(X.class);

        String[] result = writer.fieldNames();

        Arrays.sort(result);

        assertEquals(3, result.length);
        assertEquals("aStr", result[0]);
        assertEquals("anInt", result[1]);
        assertEquals("innerObj", result[2]);

        writer.push("innerObj");
        writer.create(Y.class);
        result = writer.fieldNames();

        assertEquals(1, result.length);
        assertEquals("yField", result[0]);

        writer.pop();
        result = writer.fieldNames();

        assertEquals(3, result.length);
    }

    class Z {
        boolean a;
    }

    class Z2 extends Z {
        int b;
    }

    public void testInheritance() {
        ObjectTree writer = new ReflectionObjectGraph(Z.class, objectFactory);
        writer.create(Z2.class);

        String[] result = writer.fieldNames();
        assertEquals(2, result.length);
        assertEquals("b", result[0]);
        assertEquals("a", result[1]);
    }
}

