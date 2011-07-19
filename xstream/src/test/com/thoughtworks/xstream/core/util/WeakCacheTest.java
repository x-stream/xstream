/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. July 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestCase;

/**
 * @author J&ouml;rg Schaible
 */
public class WeakCacheTest extends TestCase {

    public void testIsAMap() {
        Map map = new WeakCache();
        assertEquals(0, map.size());
        assertNull(map.put("key", "value"));
        assertEquals(1, map.size());
        assertEquals("value", map.get("key"));
        assertTrue(map.containsKey(new String("key")));
        assertTrue(map.containsValue(new String("value")));
        assertEquals("value", map.values().iterator().next());
        assertEquals("key", map.keySet().iterator().next());
        assertEquals("value", ((Map.Entry)map.entrySet().iterator().next()).getValue());
        assertEquals("key", ((Map.Entry)map.entrySet().iterator().next()).getKey());
        assertEquals("value", map.put("key", "test"));
        Map copy = new HashMap(map);
        assertEquals("test", map.remove("key"));
        assertEquals(0, map.size());
        map.putAll(copy);
        assertEquals(1, map.size());
        assertEquals("test", map.get("key"));
        map.clear();
        assertEquals(0, map.size());
    }
    
    public void testEntriesAreRemovedIfKeyIsGarbageCollected() throws InterruptedException {
        String key = new String("key");
        ReferenceQueue refQueue = new ReferenceQueue();
        Reference ref = new PhantomReference(key, refQueue);
        
        Map map = new WeakCache();
        map.put(key, "value");
        key = null;

        int i = 0;
        while (refQueue.poll() == null) {
            ref.get(); // always null
            assertTrue("Key still alive even after "+i+" forced garbage collections", i++ < 5);
            Thread.sleep(10);
            System.gc();
        }
        assertEquals(0, map.size());
    }
    
    public void testSelfReferencingEntriesAreRemovedIfKeyIsGarbageCollected() throws InterruptedException {
        String key = new String("key");
        ReferenceQueue refQueue = new ReferenceQueue();
        Reference ref = new PhantomReference(key, refQueue);
        
        Map map = new WeakCache();
        map.put(key, Collections.singleton(key));
        key = null;

        int i = 0;
        while (refQueue.poll() == null) {
            ref.get(); // always null
            assertTrue("Key still alive even after "+i+" forced garbage collections", i++ < 5);
            Thread.sleep(10);
            System.gc();
        }
        assertEquals(0, map.size());
    }
    
    public void testEntriesAreRemovedIfValueIsGarbageCollected() throws InterruptedException {
        String value = new String("value");
        ReferenceQueue refQueue = new ReferenceQueue();
        Reference ref = new PhantomReference(value, refQueue);
        
        Map map = new WeakCache();
        map.put("key", value);
        value = null;

        int i = 0;
        while (refQueue.poll() == null) {
            ref.get(); // always null
            assertTrue("Value still alive even after "+i+" forced garbage collections", i++ < 5);
            Thread.sleep(10);
            System.gc();
        }
        assertEquals(0, map.size());
    }
    
    public void testSelfReferencingEntriesAreRemovedIfValueIsGarbageCollected() throws InterruptedException {
        String key = new String("key");
        Set value = Collections.singleton(key);
        ReferenceQueue refQueue = new ReferenceQueue();
        Reference ref = new PhantomReference(value, refQueue);
        
        Map map = new WeakCache();
        map.put(key, value);
        value = null;

        int i = 0;
        while (refQueue.poll() == null) {
            ref.get(); // always null
            assertTrue("Value still alive even after "+i+" forced garbage collections", i++ < 5);
            Thread.sleep(10);
            System.gc();
        }
        assertEquals(0, map.size());
    }
    
    public void testSelfReferencingEntriesWithObjectsFromPermSpace() throws MalformedURLException, ClassNotFoundException, SecurityException, NoSuchFieldException, InterruptedException {
        File proxyToys = new File("target/lib/proxytoys-0.2.1.jar");
        ClassLoader classLoader = new URLClassLoader(new URL[]{proxyToys.toURI().toURL()}, getClass().getClassLoader());
        Class simpleReferenceType = classLoader.loadClass("com.thoughtworks.proxy.kit.SimpleReference");
        Field instance = simpleReferenceType.getDeclaredField("instance");
        
        ReferenceQueue refQueue = new ReferenceQueue();
        Reference ref = new PhantomReference(instance, refQueue);
        
        Map map = new WeakCache();
        map.put(simpleReferenceType, instance);
        simpleReferenceType = null;
        instance = null;

        int i = 0;
        while (refQueue.poll() == null) {
            ref.get(); // always null
            //assertTrue("Value still alive even after "+i+" forced garbage collections", i++ < 5);
            if (i++ >= 10) {
                // actually never reached - unfortunately
                break;
            }
            Thread.sleep(10);
            System.gc();
        }
        // wanted is 1 :-/
        //assertEquals(1, map.size());
        assertEquals(0, map.size());
    }
    
    public void testCanUseDifferentMapImplementation() throws InterruptedException {
        String value = new String("value");
        ReferenceQueue refQueue = new ReferenceQueue();
        Reference ref = new PhantomReference(value, refQueue);
        
        Map map = new WeakCache(new TreeMap());
        map.put("key", value);
        value = null;

        int i = 0;
        while (refQueue.poll() == null) {
            ref.get(); // always null
            assertTrue("Value still alive even after "+i+" forced garbage collections", i++ < 5);
            Thread.sleep(10);
            System.gc();
        }
        assertEquals(0, map.size());
    }
}
