package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CharConverterTest extends AbstractAcceptanceTest {

    public void testIndicatesNullChar() {
        assertBothWays(new Character('x'), "<char>x</char>");
        assertBothWays(new Character('\0'), "<char null=\"true\"/>");
    }

}
