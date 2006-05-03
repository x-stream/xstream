package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.JVM;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
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
    
    // TODO: Remove this, if converter is installed by default
    // protected void setUp() throws Exception {
    // super.setUp();
    // xstream = new XStream() {
    // protected MapperWrapper wrapMapper(MapperWrapper next) {
    // return new CGLIBMapper(super.wrapMapper(next));
    // }
    // };
    // xstream.registerConverter(new CGLIBEnhancedConverter(xstream.getMapper()));
    // }

    public static class DelegatingHandler implements InvocationHandler, Serializable {
        private Object delegate;

        public DelegatingHandler(Object delegate) {
            this.delegate = delegate;
        }

        public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
            return method.invoke(delegate, args);
        }
    }

    public void testSupportsClassBasedProxiesWithFactory() throws NullPointerException, MalformedURLException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HashMap.class);
        enhancer.setCallback(new DelegatingHandler(new HashMap()));
        enhancer.setUseFactory(true); // true by default
        final Map orig = (Map)enhancer.create();
        orig.put("URL", new URL("http://xstream.codehaus.org"));
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

        assertBothWays(orig, xml);
    }

    public void testSupportsClassBasedProxiesWithoutFactory() throws NullPointerException, MalformedURLException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HashMap.class);
        enhancer.setCallback(new DelegatingHandler(new HashMap()));
        enhancer.setUseFactory(false);
        final Map orig = (Map)enhancer.create();
        orig.put("URL", new URL("http://xstream.codehaus.org"));
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

        assertBothWays(orig, xml);
    }

    public void testSupportForClassBasedProxyWithAdditionalInterface() throws NullPointerException {
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
        enhancer.setUseFactory(true);
        final Map orig = (Map)enhancer.create();
        final String xml = ""
                + "<CGLIB-enhanced-proxy>\n"
                + "  <type>java.lang.Object</type>\n"
                + "  <interfaces>\n"
                + "    <java-class>java.util.Map</java-class>\n"
                + "    <java-class>java.lang.Runnable</java-class>\n"
                + "  </interfaces>\n"
                + "  <hasFactory>false</hasFactory>\n"
                + "  <net.sf.cglib.proxy.NoOp_-1/>\n"
                + "</CGLIB-enhanced-proxy>";

        final Object serialized = assertBothWays(orig, xml);
        assertTrue(serialized instanceof Map);
        assertTrue(serialized instanceof Runnable);
    }

    public void testThrowsExceptionForProxiesWithMultipleCallbacks() throws NullPointerException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setCallbacks(new Callback[]{NoOp.INSTANCE, new DelegatingHandler(null), NoOp.INSTANCE});
        enhancer.setCallbackFilter(new CallbackFilter() {
            public int accept(Method method) {
                return 0;
            }
        });
        enhancer.setInterfaces(new Class[]{Runnable.class});
        final Runnable orig = (Runnable)enhancer.create();
        try {
            xstream.toXML(orig);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertTrue(e.getMessage().toLowerCase().indexOf("multiple callbacks") >= 0);
        }
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
                + "  <hasFactory>false</hasFactory>\n"
                + "  <net.sf.cglib.proxy.NoOp_-1/>\n"
                + "  <serialVersionUID>20060804</serialVersionUID>\n"
                + "</CGLIB-enhanced-proxy>";

        final Object serialized = assertBothWays(orig, xml);
        final Field field = serialized.getClass().getDeclaredField("serialVersionUID");
        field.setAccessible(true);
        assertEquals(20060804L, field.getLong(null));
    }

    public static class InterceptingHandler implements MethodInterceptor {

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            return proxy.invokeSuper(obj, args);
        }
    }

    private final static String THRESHOLD_PARAM = "$THRESHOLD$";
    private final static String CAPACITY_PARAM = "$CAPACITY$";

    public void testSupportsInterceptedClassBasedProxies() throws NullPointerException, MalformedURLException {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HashMap.class);
        enhancer.setCallback(new InterceptingHandler());
        enhancer.setUseFactory(false);
        final Map orig = (Map)enhancer.create();
        orig.put("URL", new URL("http://xstream.codehaus.org"));
        final StringBuffer xml = new StringBuffer(""
                + "<CGLIB-enhanced-proxy>\n"
                + "  <type>java.util.HashMap</type>\n"
                + "  <interfaces/>\n"
                + "  <hasFactory>false</hasFactory>\n"
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
}
