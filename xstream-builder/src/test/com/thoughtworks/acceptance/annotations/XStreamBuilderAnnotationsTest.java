/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. October 2007 by Guilherme Silveira
 */
package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractBuilderAcceptanceTest;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.builder.XStreamBuilder;

public class XStreamBuilderAnnotationsTest extends AbstractBuilderAcceptanceTest {

    @XStreamAlias("annotated")
    public static class Annotated {
    }

    public void testHandleCorrectlyAnnotatedClasses() {

        XStreamBuilder builder = new XStreamBuilder() {
            {
                handle(Annotated.class).with(annotated());
            }
        };

        Annotated root = new Annotated();
        String expected = "<annotated/>";

        assertBothWays(builder.buildXStream(), root, expected);

    }

}
