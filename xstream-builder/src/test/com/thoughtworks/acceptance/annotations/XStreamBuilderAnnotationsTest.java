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
