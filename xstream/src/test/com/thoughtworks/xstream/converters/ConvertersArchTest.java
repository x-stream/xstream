/*
 * Copyright (C) 2006, 2007, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. July 2020 by Falko Modler
 */
package com.thoughtworks.xstream.converters;

import java.util.stream.Collectors;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.classgraph.FieldInfoList.FieldInfoFilter;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Falko Modler
 */
public class ConvertersArchTest extends TestCase {

    private static final FieldInfoFilter REFLECTION_FIELD =
            fieldInfo -> fieldInfo.isStatic() && fieldInfo.getTypeDescriptor().toString().startsWith("java.lang.reflect.");

    /**
     * Tests that no converter has a static field of type {@code java.lang.reflect.*} which hints at eager reflection access at construction time.
     * Such eager access will cause unecessary "illegal reflective access" warnings or even failures when {@code XStream} is just instantiated (not even executed).
     */
    public void testNoEagerStaticReflectionFields() {
        String violatingFields = "";

        try(ScanResult scanResult = new ClassGraph()
                .acceptPackages(ConvertersArchTest.class.getPackage().getName())
                .enableFieldInfo()
                .ignoreFieldVisibility()
                .disableJarScanning()
                .scan()) {

            violatingFields = scanResult.getAllStandardClasses().stream()
                    .flatMap(info -> info.getDeclaredFieldInfo().filter(REFLECTION_FIELD).stream())
                    .map(info -> info.getClassInfo().getName() + "." + info.getName())
                    .collect(Collectors.joining("\n\t"));
        }

        if (!violatingFields.isEmpty()) {
            Assert.fail("The following direct java.lang.reflect fields must be moved to static inner classes "
                    + " to avoid eager init:\n\t" + violatingFields);
        }
    }
}
