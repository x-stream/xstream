/*
 * Copyright (C) 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 31. December 2014 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.concurrent.Callable;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * @author J&ouml;rg Schaible
 */
public class LambdaTest extends AbstractAcceptanceTest {
    String s = "";

    public static class LambdaKeeper {
        private Callable<String> callable;
        @SuppressWarnings("unused")
        private Callable<String> referenced;
        void keep(Callable<String> c) {
            callable = c;
        }
        void reference() {
            referenced = callable;
        }
    }
    
    public void testLambdaExpression() {
        LambdaKeeper keeper = new LambdaKeeper();
        keeper.keep((Callable<String> & Serializable)() -> "result");
        
        String expected = ""
                + "<keeper>\n"
                + "  <callable class=\"" + keeper.callable.getClass().getName() + "\" resolves-to=\"java.lang.invoke.SerializedLambda\">\n"
                + "    <capturingClass>com.thoughtworks.acceptance.LambdaTest</capturingClass>\n"
                + "    <functionalInterfaceClass>java/util/concurrent/Callable</functionalInterfaceClass>\n"
                + "    <functionalInterfaceMethodName>call</functionalInterfaceMethodName>\n"
                + "    <functionalInterfaceMethodSignature>()Ljava/lang/Object;</functionalInterfaceMethodSignature>\n"
                + "    <implClass>com/thoughtworks/acceptance/LambdaTest</implClass>\n"
                + "    <implMethodName>lambda$0</implMethodName>\n" // Eclipse compiler name
                + "    <implMethodSignature>()Ljava/lang/String;</implMethodSignature>\n"
                + "    <implMethodKind>6</implMethodKind>\n"
                + "    <instantiatedMethodType>()Ljava/lang/String;</instantiatedMethodType>\n"
                + "    <capturedArgs/>\n"
                + "  </callable>\n"
                + "</keeper>";
        xstream.alias("keeper", LambdaKeeper.class);
        xstream.allowTypes(SerializedLambda.class);

        assertBothWaysNormalized(keeper, expected);

//        Object resultRoot = xstream.fromXML(expected);
//        assertNotNull(resultRoot);
    }
    
    public void testReferencedLambdaExpression() {
        LambdaKeeper keeper = new LambdaKeeper();
        keeper.keep((Callable<String> & Serializable)() -> "result");
        keeper.reference();
        
        String expected = ""
                + "<keeper>\n"
                + "  <callable class=\"" + keeper.callable.getClass().getName() + "\" resolves-to=\"java.lang.invoke.SerializedLambda\">\n"
                + "    <capturingClass>com.thoughtworks.acceptance.LambdaTest</capturingClass>\n"
                + "    <functionalInterfaceClass>java/util/concurrent/Callable</functionalInterfaceClass>\n"
                + "    <functionalInterfaceMethodName>call</functionalInterfaceMethodName>\n"
                + "    <functionalInterfaceMethodSignature>()Ljava/lang/Object;</functionalInterfaceMethodSignature>\n"
                + "    <implClass>com/thoughtworks/acceptance/LambdaTest</implClass>\n"
                + "    <implMethodName>lambda$0</implMethodName>\n" // Eclipse compiler name
                + "    <implMethodSignature>()Ljava/lang/String;</implMethodSignature>\n"
                + "    <implMethodKind>6</implMethodKind>\n"
                + "    <instantiatedMethodType>()Ljava/lang/String;</instantiatedMethodType>\n"
                + "    <capturedArgs/>\n"
                + "  </callable>\n"
                + "  <referenced class=\"" +keeper.callable.getClass().getName() + "\" reference=\"../callable\"/>\n"
                + "</keeper>";
        xstream = new XStream() {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {

                    @Override
                    public Class<?> realClass(String elementName) {
                        if (elementName.matches(".*\\$\\$Lambda\\$[0-9]+/[0-9]+"))
                            return null;
                        else
                            return super.realClass(elementName);
                    }
                };
            }
        };
        setupSecurity(xstream);
        xstream.alias("keeper", LambdaKeeper.class);
        xstream.allowTypes(SerializedLambda.class);

        assertBothWaysNormalized(keeper, expected);
    }

    private void assertBothWaysNormalized(LambdaKeeper keeper, String expected) {
        String resultXml = toXML(keeper);
        assertEquals(normalizeLambda(expected), normalizeLambda(resultXml));
        
        Object resultRoot = xstream.fromXML(resultXml);
        resultXml = toXML(resultRoot);
        assertEquals(normalizeLambda(expected), normalizeLambda(resultXml));
    }

    private String normalizeLambda(String xml) {
        return xml.replaceAll("\\$\\$Lambda\\$[/0-9]+\"", "\\$\\$Lambda\\$\"").replaceAll(">lambda\\$[^<]+<", ">lambda\\$0<");
    }
}
