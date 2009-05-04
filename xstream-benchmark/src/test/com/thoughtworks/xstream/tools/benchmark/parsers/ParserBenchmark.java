/*
 * Copyright (C) 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 18. February 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.parsers;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.tools.benchmark.Harness;
import com.thoughtworks.xstream.tools.benchmark.metrics.DeserializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamDom;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamDom4J;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamBEAStax;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamJDom;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamKXml2;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamKXml2DOM;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamSjsxp;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamWoodstox;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamXom;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamXpp3;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamXpp3DOM;
import com.thoughtworks.xstream.tools.benchmark.reporters.TextReporter;
import com.thoughtworks.xstream.tools.benchmark.targets.BasicTarget;
import com.thoughtworks.xstream.tools.benchmark.targets.ExtendedTarget;
import com.thoughtworks.xstream.tools.benchmark.targets.JavaBeanTarget;
import com.thoughtworks.xstream.tools.benchmark.targets.ReflectionTarget;
import com.thoughtworks.xstream.tools.benchmark.targets.SerializableTarget;
import com.thoughtworks.xstream.tools.model.targets.FieldReflection;
import com.thoughtworks.xstream.tools.model.targets.HierarchyLevelReflection;
import com.thoughtworks.xstream.tools.model.targets.InnerClassesReflection;
import com.thoughtworks.xstream.tools.model.targets.StaticInnerClassesReflection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

import java.io.PrintWriter;


/**
 * Main application to run harness for Profile benchmark.
 * 
 * @author J&ouml;rg Schaible
 */
public class ParserBenchmark {
    public static void main(String[] args) {
        int counter = 1000;
        
        Options options = new Options();
        options.addOption("p", "product", true, "Class name of the product to use for benchmark");
        options.addOption("n", true, "Number of repetitions");

        Harness harness = new Harness();
        harness.addMetric(new DeserializationSpeedMetric(0, false) {
            public String toString() {
                return "Initial run deserialization";
            }
        });

        Parser parser = new PosixParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            String name = null;
            if (commandLine.hasOption('p')) {
                name = commandLine.getOptionValue('p');
            }
            if (name == null || name.equals("DOM")) {
                harness.addProduct(new XStreamDom());
            } 
            if (name == null || name.equals("JDOM")) {
                harness.addProduct(new XStreamJDom());
            } 
            if (name == null || name.equals("DOM4J")) {
                harness.addProduct(new XStreamDom4J());
            } 
            if (name == null || name.equals("XOM")) {
                harness.addProduct(new XStreamXom());
            } 
            if (name == null || name.equals("BEAStAX")) {
                harness.addProduct(new XStreamBEAStax());
            } 
            if (name == null || name.equals("Woodstox")) {
                harness.addProduct(new XStreamWoodstox());
            } 
            if (JVM.is16() && (name == null || name.equals("SJSXP"))) {
                harness.addProduct(new XStreamSjsxp());
            } 
            if (name == null || name.equals("Xpp3")) {
                harness.addProduct(new XStreamXpp3());
            } 
            if (name == null || name.equals("kXML2")) {
                harness.addProduct(new XStreamKXml2());
            }
            if (name == null || name.equals("Xpp3DOM")) {
                harness.addProduct(new XStreamXpp3DOM());
            } 
            if (name == null || name.equals("kXML2DOM")) {
                harness.addProduct(new XStreamKXml2DOM());
            }
            if (commandLine.hasOption('n')) {
                counter = Integer.parseInt(commandLine.getOptionValue('n'));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        harness.addMetric(new DeserializationSpeedMetric(counter, false));
        harness.addTarget(new BasicTarget());
        harness.addTarget(new ExtendedTarget());
        harness.addTarget(new ReflectionTarget());
        harness.addTarget(new SerializableTarget());
        harness.addTarget(new JavaBeanTarget());
        if (false) {
        harness.addTarget(new FieldReflection());
        harness.addTarget(new HierarchyLevelReflection());
        harness.addTarget(new InnerClassesReflection());
        harness.addTarget(new StaticInnerClassesReflection());
        }
        harness.run(new TextReporter(new PrintWriter(System.out, true)));
        System.out.println("Done.");
    }
}
