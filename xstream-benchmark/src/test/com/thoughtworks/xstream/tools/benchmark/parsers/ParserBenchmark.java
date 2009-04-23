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

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.io.xml.JDomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XomDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.io.xml.XppReader;
import com.thoughtworks.xstream.tools.benchmark.Harness;
import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.tools.benchmark.metrics.DeserializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.metrics.SerializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamDom;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamDriver;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamStax;
import com.thoughtworks.xstream.tools.benchmark.products.XStreamXpp;
import com.thoughtworks.xstream.tools.benchmark.reporters.TextReporter;
import com.thoughtworks.xstream.tools.benchmark.targets.BasicTarget;
import com.thoughtworks.xstream.tools.benchmark.targets.ExtendedTarget;
import com.thoughtworks.xstream.tools.benchmark.targets.ReflectionTarget;
import com.thoughtworks.xstream.tools.benchmark.targets.SerializableTarget;
import com.thoughtworks.xstream.tools.model.targets.FieldReflection;
import com.thoughtworks.xstream.tools.model.targets.HierarchyLevelReflection;
import com.thoughtworks.xstream.tools.model.targets.InnerClassesReflection;
import com.thoughtworks.xstream.tools.model.targets.StaticInnerClassesReflection;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;


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
                harness.addProduct(new XStreamDriver(new JDomDriver(), "XML with JDOM parser"));
            } 
            if (name == null || name.equals("DOM4J")) {
                harness.addProduct(new XStreamDriver(new Dom4JDriver() {

                    public HierarchicalStreamWriter createWriter(Writer writer) {
                        return new PrettyPrintWriter(writer);
                    }
                    
                }, "XML with DOM4J parser"));
            } 
            if (name == null || name.equals("XOM")) {
                harness.addProduct(new XStreamDriver(new XomDriver(), "XML with XOM parser"));
            } 
            if (name == null || name.equals("StAX")) {
                harness.addProduct(new XStreamStax());
            } 
            if (name == null || name.equals("Xpp3")) {
                harness.addProduct(new XStreamXpp());
            } 
            if (name == null || name.equals("kXML")) {
                harness.addProduct(new XStreamDriver(new XppDriver() {
                    public HierarchicalStreamReader createReader(Reader xml) {
                        return new XppReader(xml, xmlFriendlyReplacer()) {

                            protected XmlPullParser createParser() {
                                return new KXmlParser();
                            }
                            
                        };
                    }
                    
                }, "XML with kXML parser"));
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
//        harness.addTarget(new FieldReflection());
//        harness.addTarget(new HierarchyLevelReflection());
//        harness.addTarget(new InnerClassesReflection());
//        harness.addTarget(new StaticInnerClassesReflection());
        harness.run(new TextReporter(new PrintWriter(System.out, true)));
        System.out.println("Done.");
    }
}
