package com.thoughtworks.xstream.objecttree.reflection;

import com.thoughtworks.someobjects.X;
import com.thoughtworks.someobjects.Y;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import junit.framework.TestCase;

public class ObjectGraphReaderTest extends TestCase {
    private X x;
    private Y y;
    private ObjectTree reader;
    private ObjectFactory objectFactory = new SunReflectionObjectFactory();

    protected void setUp() throws Exception {
        super.setUp();
        x = new X();
        x.aStr = "hello";
        x.anInt = 22;

        y = new Y();
        y.yField = "world";
        x.innerObj = y;

        reader = new ReflectionObjectGraph(x, objectFactory);
    }

    public void testReadsSimpleFieldsOfAnObject() {
        reader.push("aStr");
        assertEquals("hello", reader.get());
        reader.pop();

        reader.push("anInt");
        assertEquals(new Integer(22), reader.get());
        reader.pop();

        reader.push("innerObj");
        assertEquals(y, reader.get());
        reader.pop();
    }

    public void testReadsFieldsOfNestedObjects() {
        reader.push("aStr");
        assertEquals("hello", reader.get());
        reader.pop();

        reader.push("innerObj");
        reader.push("yField");
        assertEquals("world", reader.get());
        reader.pop();
        reader.pop();

        reader.push("anInt");
        assertEquals(new Integer(22), reader.get());
        reader.pop();
    }

}
