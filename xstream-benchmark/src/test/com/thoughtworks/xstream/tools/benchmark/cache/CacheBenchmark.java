/*
 * Copyright (C) 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.cache;

import com.thoughtworks.xstream.tools.benchmark.Harness;
import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.tools.benchmark.cache.products.AliasedAttributeCache;
import com.thoughtworks.xstream.tools.benchmark.cache.products.Cache122;
import com.thoughtworks.xstream.tools.benchmark.cache.products.DefaultImplementationCache;
import com.thoughtworks.xstream.tools.benchmark.cache.products.NoCache;
import com.thoughtworks.xstream.tools.benchmark.cache.products.RealClassCache;
import com.thoughtworks.xstream.tools.benchmark.cache.products.SerializedClassCache;
import com.thoughtworks.xstream.tools.benchmark.metrics.DeserializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.metrics.SerializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.reporters.TextReporter;
import com.thoughtworks.xstream.tools.benchmark.targets.BasicTarget;
import com.thoughtworks.xstream.tools.benchmark.targets.ExtendedTarget;
import com.thoughtworks.xstream.tools.benchmark.targets.ReflectionTarget;
import com.thoughtworks.xstream.tools.benchmark.targets.SerializableTarget;

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
public class CacheBenchmark {
    public static void main(String[] args) {
        int counter = 10000;
        Product product = null;
        
        Options options = new Options();
        options.addOption("p", "product", true, "Class name of the product to use for benchmark");
        options.addOption("n", true, "Number of repetitions");
        
        Parser parser = new PosixParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption('p')) {
                product = (Product)Class.forName(commandLine.getOptionValue('p')).newInstance();
            }
            if (commandLine.hasOption('n')) {
                counter = Integer.parseInt(commandLine.getOptionValue('n'));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        Harness harness = new Harness();
        // harness.addMetric(new SerializationSpeedMetric(1) {
        // public String toString() {
        // return "Initial run serialization";
        // }
        // });
        // harness.addMetric(new DeserializationSpeedMetric(1, false) {
        // public String toString() {
        // return "Initial run deserialization";
        // }
        // });
        harness.addMetric(new SerializationSpeedMetric(counter));
        harness.addMetric(new DeserializationSpeedMetric(counter, false));
        if (product == null) {
            harness.addProduct(new NoCache());
            harness.addProduct(new Cache122());
            harness.addProduct(new RealClassCache());
            harness.addProduct(new SerializedClassCache());
            harness.addProduct(new AliasedAttributeCache());
            harness.addProduct(new DefaultImplementationCache());
            harness.addProduct(new NoCache());
        } else {
            harness.addProduct(product);
        }
        harness.addTarget(new BasicTarget());
        harness.addTarget(new ExtendedTarget());
        harness.addTarget(new ReflectionTarget());
        harness.addTarget(new SerializableTarget());
        harness.run(new TextReporter(new PrintWriter(System.out, true)));
        System.out.println("Done.");
    }
}
