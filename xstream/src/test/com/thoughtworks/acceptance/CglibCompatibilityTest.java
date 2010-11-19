/*
 * Copyright (C) 2006, 2007, 2008, 2010 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. April 2006 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.reflection.CGLIBEnhancedConverter;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.mapper.CGLIBMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * @author J&ouml;rg Schaible
 */
public class CglibCompatibilityTest extends AbstractAcceptanceTest {

    protected XStream createXStream() {
        XStream xstream = new XStream(createDriver()) {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new CGLIBMapper(next);
            }
        };
        xstream.registerConverter(new CGLIBEnhancedConverter(xstream.getMapper(), xstream
            .getReflectionProvider(), xstream.getClassLoader()));
        return xstream;
    }

    public static class DelegatingHandler implements InvocationHandler, Serializable {
        private Object delegate;

        public DelegatingHandler(Object delegate) {
            this.delegate = delegate;
        }

        public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
            return method.invoke(delegate, args);
        }
    }

    public static class DelegatingInterceptor implements MethodInterceptor, Serializable,
        Runnable {
        private Object delegate;

        public DelegatingInterceptor(Object delegate) {
            this.delegate = delegate;
        }

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
            throws Throwable {
            return method.invoke(delegate, args);
        }

        public void run() {
        }
    }

    public static class DelegatingDispatcher implements Dispatcher, Serializable {
        private Object delegate;

        public DelegatingDispatcher(Object delegate) {
            this.delegate = delegate;
        }

        public Object loadObject() throws Exception {
            return delegate;
        }
    }

    public void testSupportsClassBasedProxiesWithFactory()
        throws NullPointerException, MalformedURLException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HashMap.class);
        enhancer.setCallback(new DelegatingHandler(new HashMap()));
        enhancer.setUseFactory(true); // true by default
        final Map orig = (Map)enhancer.create();
        final URL url = new URL("http://xstream.codehaus.org");
        orig.put("URL", url);
        final String xml = ""
            + "<CGLIB-enhanced-proxy>\n"
            + "  <type>java.util.HashMap</type>\n"
            + "  <interfaces/>\n"
            + "  <hasFactory>true</hasFactory>\n"
            + "  <com.thoughtworks.acceptance.CglibCompatibilityTest_-DelegatingHandler>\n"
            + "    <delegate class=\"map\">\n"
            + "      <entry>\n"
            + "        <string>URL</string>\n"
            + "        <url>http://xstream.codehaus.org</url>\n"
            + "      </entry>\n"
            + "    </delegate>\n"
            + "  </com.thoughtworks.acceptance.CglibCompatibilityTest_-DelegatingHandler>\n"
            + "</CGLIB-enhanced-proxy>";

        final Map serialized = (Map)assertBothWays(orig, xml);
        assertEquals(url, serialized.get("URL"));
    }

    public void testSupportsClassBasedProxiesWithoutFactory()
        throws NullPointerException, MalformedURLException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HashMap.class);
        enhancer.setCallback(new DelegatingHandler(new HashMap()));
        enhancer.setUseFactory(false);
        final Map orig = (Map)enhancer.create();
        final URL url = new URL("http://xstream.codehaus.org");
        orig.put("URL", url);
        final String xml = ""
            + "<CGLIB-enhanced-proxy>\n"
            + "  <type>java.util.HashMap</type>\n"
            + "  <interfaces/>\n"
            + "  <hasFactory>false</hasFactory>\n"
            + "  <com.thoughtworks.acceptance.CglibCompatibilityTest_-DelegatingHandler>\n"
            + "    <delegate class=\"map\">\n"
            + "      <entry>\n"
            + "        <string>URL</string>\n"
            + "        <url>http://xstream.codehaus.org</url>\n"
            + "      </entry>\n"
            + "    </delegate>\n"
            + "  </com.thoughtworks.acceptance.CglibCompatibilityTest_-DelegatingHandler>\n"
            + "</CGLIB-enhanced-proxy>";

        final Map serialized = (Map)assertBothWays(orig, xml);
        assertEquals(url, serialized.get("URL"));
    }

    public void testSupportForClassBasedProxyWithAdditionalInterface()
        throws NullPointerException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HashMap.class);
        enhancer.setCallback(NoOp.INSTANCE);
        enhancer.setInterfaces(new Class[]{Runnable.class});
        final Map orig = (Map)enhancer.create();
        final String xml = ""
            + "<CGLIB-enhanced-proxy>\n"
            + "  <type>java.util.HashMap</type>\n"
            + "  <interfaces>\n"
            + "    <java-class>java.lang.Runnable</java-class>\n"
            + "  </interfaces>\n"
            + "  <hasFactory>true</hasFactory>\n"
            + "  <net.sf.cglib.proxy.NoOp_-1/>\n"
            + "</CGLIB-enhanced-proxy>";

        final Object serialized = assertBothWays(orig, xml);
        assertTrue(serialized instanceof HashMap);
        assertTrue(serialized instanceof Map);
        assertTrue(serialized instanceof Runnable);
    }

    public void testSupportsProxiesWithMultipleInterfaces() throws NullPointerException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setCallback(NoOp.INSTANCE);
        enhancer.setInterfaces(new Class[]{Map.class, Runnable.class});
        final Map orig = (Map)enhancer.create();
        final String xml = ""
            + "<CGLIB-enhanced-proxy>\n"
            + "  <type>java.lang.Object</type>\n"
            + "  <interfaces>\n"
            + "    <java-class>java.util.Map</java-class>\n"
            + "    <java-class>java.lang.Runnable</java-class>\n"
            + "  </interfaces>\n"
            + "  <hasFactory>true</hasFactory>\n"
            + "  <net.sf.cglib.proxy.NoOp_-1/>\n"
            + "</CGLIB-enhanced-proxy>";

        final Object serialized = assertBothWays(orig, xml);
        assertTrue(serialized instanceof Map);
        assertTrue(serialized instanceof Runnable);
    }

    public void testSupportProxiesUsingFactoryWithMultipleCallbacks()
        throws NullPointerException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setCallbacks(new Callback[]{

            new DelegatingInterceptor(null), new DelegatingHandler(null),
            new DelegatingDispatcher(null), NoOp.INSTANCE});
        enhancer.setCallbackFilter(new CallbackFilter() {
            int i = 1;

            public int accept(Method method) {
                if (method.getDeclaringClass() == Runnable.class) {
                    return 0;
                }
                return i < 3 ? i++ : i;
            }
        });
        enhancer.setInterfaces(new Class[]{Runnable.class});
        enhancer.setUseFactory(true);
        final Runnable orig = (Runnable)enhancer.create();
        final String xml = xstream.toXML(orig);
        final Factory deserialized = (Factory)xstream.fromXML(xml);
        assertTrue("Not a Runnable anymore", deserialized instanceof Runnable);
        Callback[] callbacks = deserialized.getCallbacks();
        assertEquals(4, callbacks.length);
        assertTrue(callbacks[0] instanceof DelegatingInterceptor);
        assertTrue(callbacks[1] instanceof DelegatingHandler);
        assertTrue(callbacks[2] instanceof DelegatingDispatcher);
        assertTrue(callbacks[3] instanceof NoOp);
    }

    public void testThrowsExceptionForProxiesNotUsingFactoryWithMultipleCallbacks()
        throws NullPointerException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setCallbacks(new Callback[]{

            new DelegatingInterceptor(null), new DelegatingHandler(null),
            new DelegatingDispatcher(null), NoOp.INSTANCE});
        enhancer.setCallbackFilter(new CallbackFilter() {
            int i = 1;

            public int accept(Method method) {
                if (method.getDeclaringClass() == Runnable.class) {
                    return 0;
                }
                return i < 3 ? i++ : i;
            }
        });
        enhancer.setInterfaces(new Class[]{Runnable.class});
        enhancer.setUseFactory(false);
        final Runnable orig = (Runnable)enhancer.create();
        try {
            xstream.toXML(orig);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            
        }
    }

    public void testSupportProxiesWithMultipleCallbackSetToNull() throws NullPointerException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HashMap.class);
        enhancer.setCallback(NoOp.INSTANCE);
        final HashMap orig = (HashMap)enhancer.create();
        ((Factory)orig).setCallback(0, null);
        final String xml = ""
            + "<CGLIB-enhanced-proxy>\n"
            + "  <type>java.util.HashMap</type>\n"
            + "  <interfaces/>\n"
            + "  <hasFactory>true</hasFactory>\n"
            + "  <null/>\n"
            + "</CGLIB-enhanced-proxy>";

        assertBothWays(orig, xml);
    }

    public void testSupportsSerialVersionUID()
        throws NullPointerException, NoSuchFieldException, IllegalAccessException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setCallback(NoOp.INSTANCE);
        enhancer.setInterfaces(new Class[]{Runnable.class});
        enhancer.setSerialVersionUID(new Long(20060804L));
        final Runnable orig = (Runnable)enhancer.create();
        final String xml = ""
            + "<CGLIB-enhanced-proxy>\n"
            + "  <type>java.lang.Object</type>\n"
            + "  <interfaces>\n"
            + "    <java-class>java.lang.Runnable</java-class>\n"
            + "  </interfaces>\n"
            + "  <hasFactory>true</hasFactory>\n"
            + "  <net.sf.cglib.proxy.NoOp_-1/>\n"
            + "  <serialVersionUID>20060804</serialVersionUID>\n"
            + "</CGLIB-enhanced-proxy>";

        final Object serialized = assertBothWays(orig, xml);
        final Field field = serialized.getClass().getDeclaredField("serialVersionUID");
        field.setAccessible(true);
        assertEquals(20060804L, field.getLong(null));
    }

    public static class InterceptingHandler implements MethodInterceptor {

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
            throws Throwable {
            return proxy.invokeSuper(obj, args);
        }
    }

    private final static String THRESHOLD_PARAM = "$THRESHOLD$";
    private final static String CAPACITY_PARAM = "$CAPACITY$";

    public void testSupportsInterceptedClassBasedProxies()
        throws NullPointerException, MalformedURLException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HashMap.class);
        enhancer.setCallback(new InterceptingHandler());
        enhancer.setUseFactory(true);
        final Map orig = (Map)enhancer.create();
        orig.put("URL", new URL("http://xstream.codehaus.org"));
        final StringBuffer xml = new StringBuffer(""
            + "<CGLIB-enhanced-proxy>\n"
            + "  <type>java.util.HashMap</type>\n"
            + "  <interfaces/>\n"
            + "  <hasFactory>true</hasFactory>\n"
            + "  <com.thoughtworks.acceptance.CglibCompatibilityTest_-InterceptingHandler/>\n"
            + "  <instance serialization=\"custom\">\n"
            + "    <unserializable-parents/>\n"
            + "    <map>\n"
            + "      <default>\n"
            + "        <loadFactor>0.75</loadFactor>\n"
            + "        <threshold>$THRESHOLD$</threshold>\n"
            + "      </default>\n"
            + "      <int>$CAPACITY$</int>\n"
            + "      <int>1</int>\n"
            + "      <string>URL</string>\n"
            + "      <url>http://xstream.codehaus.org</url>\n"
            + "    </map>\n"
            + "  </instance>\n"
            + "</CGLIB-enhanced-proxy>");

        // JDK 1.3 has different threshold and capacity algorithms
        int idx = xml.toString().indexOf(THRESHOLD_PARAM);
        xml.replace(idx, idx + THRESHOLD_PARAM.length(), JVM.is14() ? "12" : "8");
        idx = xml.toString().indexOf(CAPACITY_PARAM);
        xml.replace(idx, idx + CAPACITY_PARAM.length(), JVM.is14() ? "16" : "11");

        Map serialized = (Map)assertBothWays(orig, xml.toString());
        assertEquals(orig.toString(), serialized.toString());
    }

    public static class ClassWithProxyMember {
        Runnable runnable;
        Map map;
    };

    public void testSupportsProxiesAsFieldMember() throws NullPointerException {
        ClassWithProxyMember expected = new ClassWithProxyMember();
        xstream.alias("with-proxy", ClassWithProxyMember.class);
        final Enhancer enhancer = new Enhancer();
        enhancer.setCallback(NoOp.INSTANCE);
        enhancer.setInterfaces(new Class[]{Map.class, Runnable.class});
        final Map orig = (Map)enhancer.create();
        expected.runnable = (Runnable)orig;
        expected.map = orig;
        final String xml = ""
            + "<with-proxy>\n"
            + "  <runnable class=\"CGLIB-enhanced-proxy\">\n"
            + "    <type>java.lang.Object</type>\n"
            + "    <interfaces>\n"
            + "      <java-class>java.util.Map</java-class>\n"
            + "      <java-class>java.lang.Runnable</java-class>\n"
            + "    </interfaces>\n"
            + "    <hasFactory>true</hasFactory>\n"
            + "    <net.sf.cglib.proxy.NoOp_-1/>\n"
            + "  </runnable>\n"
            + "  <map class=\"CGLIB-enhanced-proxy\" reference=\"../runnable\"/>\n"
            + "</with-proxy>";

        final Object serialized = assertBothWays(expected, xml);
        assertTrue(serialized instanceof ClassWithProxyMember);
    }

    public void testProxyTypeCanBeAliased() throws MalformedURLException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HashMap.class);
        enhancer.setCallback(new DelegatingHandler(new HashMap()));
        final Map orig = (Map)enhancer.create();
        orig.put("URL", new URL("http://xstream.codehaus.org"));
        xstream.aliasType("cglib", Map.class);
        final String expected = ""
            + "<cglib>\n"
            + "  <type>java.util.HashMap</type>\n"
            + "  <interfaces/>\n"
            + "  <hasFactory>true</hasFactory>\n"
            + "  <com.thoughtworks.acceptance.CglibCompatibilityTest_-DelegatingHandler>\n"
            + "    <delegate class=\"map\">\n"
            + "      <entry>\n"
            + "        <string>URL</string>\n"
            + "        <url>http://xstream.codehaus.org</url>\n"
            + "      </entry>\n"
            + "    </delegate>\n"
            + "  </com.thoughtworks.acceptance.CglibCompatibilityTest_-DelegatingHandler>\n"
            + "</cglib>";
        assertEquals(expected, xstream.toXML(orig));
    }
}
