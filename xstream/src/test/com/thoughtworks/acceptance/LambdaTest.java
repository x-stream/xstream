/*
 * Copyright (C) 2014, 2015, 2018, 2019 XStream Committers.
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
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;


/**
 * @author J&ouml;rg Schaible
 */
public class LambdaTest extends AbstractAcceptanceTest {
    public static class LambdaKeeper {
        private Callable<String> callable;
        @SuppressWarnings("unused")
        private Object referenced;

        void keep(final Callable<String> c) {
            callable = c;
        }

        void reference() {
            referenced = callable;
        }
    }

    public void testLambdaExpression() {
        final LambdaKeeper keeper = new LambdaKeeper();
        keeper.keep(() -> "result");

        final String expected = "" + "<keeper>\n" + "  <callable class=\"null\"/>\n" + "</keeper>";
        xstream.alias("keeper", LambdaKeeper.class);
        xstream.allowTypes(SerializedLambda.class);

        assertEquals(expected, xstream.toXML(keeper));
        assertBothWays(xstream.fromXML(expected), "<keeper/>");
    }

    public void testSerializableLambdaExpression() {
        final LambdaKeeper keeper = new LambdaKeeper();
        keeper.keep(() -> "result");

        final String expected = ""
            + "<keeper>\n"
            + "  <callable resolves-to=\"serialized-lambda\">\n"
            + "    <capturingClass>com.thoughtworks.acceptance.LambdaTest</capturingClass>\n"
            + "    <functionalInterfaceClass>java/util/concurrent/Callable</functionalInterfaceClass>\n"
            + "    <functionalInterfaceMethodName>call</functionalInterfaceMethodName>\n"
            + "    <functionalInterfaceMethodSignature>()Ljava/lang/Object;</functionalInterfaceMethodSignature>\n"
            + "    <implClass>com/thoughtworks/acceptance/LambdaTest</implClass>\n"
            + "    <implMethodName>lambda$0</implMethodName>\n"
            + "    <implMethodSignature>()Ljava/lang/String;</implMethodSignature>\n"
            + "    <implMethodKind>6</implMethodKind>\n"
            + "    <instantiatedMethodType>()Ljava/lang/String;</instantiatedMethodType>\n"
            + "    <capturedArgs/>\n"
            + "  </callable>\n"
            + "</keeper>";
        xstream.alias("keeper", LambdaKeeper.class);
        xstream.allowTypes(SerializedLambda.class);

        assertBothWaysNormalized(keeper, expected);

        // ... deserialization fails if code was compiled with compiler of different vendor
        // Object resultRoot = xstream.fromXML(expected);
        // assertNotNull(resultRoot);
    }

    public void testReferencedLambdaExpression() {
        final LambdaKeeper keeper = new LambdaKeeper();
        keeper.keep(() -> "result");
        keeper.reference();

        final String expected = ""
            + "<keeper>\n"
            + "  <callable resolves-to=\"serialized-lambda\">\n"
            + "    <capturingClass>com.thoughtworks.acceptance.LambdaTest</capturingClass>\n"
            + "    <functionalInterfaceClass>java/util/concurrent/Callable</functionalInterfaceClass>\n"
            + "    <functionalInterfaceMethodName>call</functionalInterfaceMethodName>\n"
            + "    <functionalInterfaceMethodSignature>()Ljava/lang/Object;</functionalInterfaceMethodSignature>\n"
            + "    <implClass>com/thoughtworks/acceptance/LambdaTest</implClass>\n"
            + "    <implMethodName>lambda$0</implMethodName>\n"
            + "    <implMethodSignature>()Ljava/lang/String;</implMethodSignature>\n"
            + "    <implMethodKind>6</implMethodKind>\n"
            + "    <instantiatedMethodType>()Ljava/lang/String;</instantiatedMethodType>\n"
            + "    <capturedArgs/>\n"
            + "  </callable>\n"
            + "  <referenced class=\"java.util.concurrent.Callable\" reference=\"../callable\"/>\n"
            + "</keeper>";
        xstream.alias("keeper", LambdaKeeper.class);
        xstream.allowTypes(SerializedLambda.class);

        assertBothWaysNormalized(keeper, expected);
    }

    public interface X {
        static int getTwo() {
            return 2;
        }

        default int getOne() {
            return 1;
        }
    }

    public interface SerializableCallable<T> extends X, Serializable, Callable<T> {}

    public void testLambdaArray() {
        @SuppressWarnings("unchecked")
        final Object[] lambdas = {
            (Callable<String> & Serializable)() -> "result", (SerializableCallable<String>)() -> "result",
            (Runnable & Serializable)() -> run(), (X & Serializable & Callable<String>)() -> "result",
            (Runnable)() -> run(), null};
        lambdas[lambdas.length - 1] = lambdas[0];

        final String expected = ""
            + "<object-array>\n"
            + "  <callable resolves-to=\"serialized-lambda\">\n"
            + "    <capturingClass>com.thoughtworks.acceptance.LambdaTest</capturingClass>\n"
            + "    <functionalInterfaceClass>java/util/concurrent/Callable</functionalInterfaceClass>\n"
            + "    <functionalInterfaceMethodName>call</functionalInterfaceMethodName>\n"
            + "    <functionalInterfaceMethodSignature>()Ljava/lang/Object;</functionalInterfaceMethodSignature>\n"
            + "    <implClass>com/thoughtworks/acceptance/LambdaTest</implClass>\n"
            + "    <implMethodName>lambda$0</implMethodName>\n"
            + "    <implMethodSignature>()Ljava/lang/String;</implMethodSignature>\n"
            + "    <implMethodKind>6</implMethodKind>\n"
            + "    <instantiatedMethodType>()Ljava/lang/String;</instantiatedMethodType>\n"
            + "    <capturedArgs/>\n"
            + "  </callable>\n"
            + "  <serializable-callable resolves-to=\"serialized-lambda\">\n"
            + "    <capturingClass>com.thoughtworks.acceptance.LambdaTest</capturingClass>\n"
            + "    <functionalInterfaceClass>com/thoughtworks/acceptance/LambdaTest$SerializableCallable</functionalInterfaceClass>\n"
            + "    <functionalInterfaceMethodName>call</functionalInterfaceMethodName>\n"
            + "    <functionalInterfaceMethodSignature>()Ljava/lang/Object;</functionalInterfaceMethodSignature>\n"
            + "    <implClass>com/thoughtworks/acceptance/LambdaTest</implClass>\n"
            + "    <implMethodName>lambda$0</implMethodName>\n"
            + "    <implMethodSignature>()Ljava/lang/String;</implMethodSignature>\n"
            + "    <implMethodKind>6</implMethodKind>\n"
            + "    <instantiatedMethodType>()Ljava/lang/String;</instantiatedMethodType>\n"
            + "    <capturedArgs/>\n"
            + "  </serializable-callable>\n"
            + "  <runnable resolves-to=\"serialized-lambda\">\n"
            + "    <capturingClass>com.thoughtworks.acceptance.LambdaTest</capturingClass>\n"
            + "    <functionalInterfaceClass>java/lang/Runnable</functionalInterfaceClass>\n"
            + "    <functionalInterfaceMethodName>run</functionalInterfaceMethodName>\n"
            + "    <functionalInterfaceMethodSignature>()V</functionalInterfaceMethodSignature>\n"
            + "    <implClass>com/thoughtworks/acceptance/LambdaTest</implClass>\n"
            + "    <implMethodName>lambda$0</implMethodName>\n"
            + "    <implMethodSignature>()V</implMethodSignature>\n"
            + "    <implMethodKind>7</implMethodKind>\n"
            + "    <instantiatedMethodType>()V</instantiatedMethodType>\n"
            + "    <capturedArgs>\n"
            + "      <com.thoughtworks.acceptance.LambdaTest>\n"
            + "        <fName>testLambdaArray</fName>\n"
            + "      </com.thoughtworks.acceptance.LambdaTest>\n"
            + "    </capturedArgs>\n"
            + "  </runnable>\n"
            + "  <callable resolves-to=\"serialized-lambda\">\n"
            + "    <capturingClass>com.thoughtworks.acceptance.LambdaTest</capturingClass>\n"
            + "    <functionalInterfaceClass>java/util/concurrent/Callable</functionalInterfaceClass>\n"
            + "    <functionalInterfaceMethodName>call</functionalInterfaceMethodName>\n"
            + "    <functionalInterfaceMethodSignature>()Ljava/lang/Object;</functionalInterfaceMethodSignature>\n"
            + "    <implClass>com/thoughtworks/acceptance/LambdaTest</implClass>\n"
            + "    <implMethodName>lambda$0</implMethodName>\n"
            + "    <implMethodSignature>()Ljava/lang/String;</implMethodSignature>\n"
            + "    <implMethodKind>6</implMethodKind>\n"
            + "    <instantiatedMethodType>()Ljava/lang/String;</instantiatedMethodType>\n"
            + "    <capturedArgs/>\n"
            + "  </callable>\n"
            + "  <null/>\n"
            + "  <callable reference=\"../callable\"/>\n"
            + "</object-array>";
        xstream.alias("callable", Callable.class);
        xstream.alias("runnable", Runnable.class);
        xstream.alias("serializable-callable", SerializableCallable.class);
        xstream.allowTypes(SerializedLambda.class, LambdaTest.class);

        assertBothWaysNormalized(lambdas, expected);
    }

    private void assertBothWaysNormalized(final Object original, final String expected) {
        String resultXml = toXML(original);
        assertEquals(normalizeLambda(expected), normalizeLambda(resultXml));

        final Object resultRoot = xstream.fromXML(resultXml);
        resultXml = toXML(resultRoot);
        assertEquals(normalizeLambda(expected), normalizeLambda(resultXml));
    }

    private String normalizeLambda(final String xml) {
        // unify compiler specific name for implMethodName, Eclipse uses always "lambda$0"
        return xml.replaceAll(">lambda\\$[^<]+<", ">lambda\\$0<");
    }

    public void testTreeSetWithLambda() {
        final Set<String> set = new TreeSet<String>(Comparator.<String>comparingInt(s -> -s.length()));
        set.add("L");
        set.add("XXL");
        set.add("XL");

        final String expected = ""
            + "<sorted-set>\n"
            + "  <comparator class=\"java.util.Comparator\" resolves-to=\"serialized-lambda\">\n"
            + "    <capturingClass>java.util.Comparator</capturingClass>\n"
            + "    <functionalInterfaceClass>java/util/Comparator</functionalInterfaceClass>\n"
            + "    <functionalInterfaceMethodName>compare</functionalInterfaceMethodName>\n"
            + "    <functionalInterfaceMethodSignature>(Ljava/lang/Object;Ljava/lang/Object;)I</functionalInterfaceMethodSignature>\n"
            + "    <implClass>java/util/Comparator</implClass>\n"
            + "    <implMethodName>lambda$0</implMethodName>\n"
            + "    <implMethodSignature>(Ljava/util/function/ToIntFunction;Ljava/lang/Object;Ljava/lang/Object;)I</implMethodSignature>\n"
            + "    <implMethodKind>6</implMethodKind>\n"
            + "    <instantiatedMethodType>(Ljava/lang/Object;Ljava/lang/Object;)I</instantiatedMethodType>\n"
            + "    <capturedArgs>\n"
            + "      <null/>\n"
            + "    </capturedArgs>\n"
            + "  </comparator>\n"
            + "  <string>XXL</string>\n"
            + "  <string>XL</string>\n"
            + "  <string>L</string>\n"
            + "</sorted-set>";
        xstream.allowTypes(SerializedLambda.class);
        
        assertBothWaysNormalized(set, expected);
    }
}
