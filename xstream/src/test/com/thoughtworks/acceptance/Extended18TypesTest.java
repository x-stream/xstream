/*
 * Copyright (C) 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 7. December 2022 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.thoughtworks.xstream.XStream;


/**
 * @author J&ouml;rg Schaible
 */
public class Extended18TypesTest extends AbstractAcceptanceTest {

    @Override
    protected void setupSecurity(final XStream xstream) {
        super.setupSecurity(xstream);
    }

    public void testEmptyOptional() {
        final Optional<Object> optional = Optional.empty();
        assertBothWays(optional, "<optional/>");
    }

    public void testOptional() {
        final Optional<String> optional = Optional.of("test");
        assertBothWays(optional, ("" //
            + "<optional>\n" //
            + "  <value class='string'>test</value>\n" //
            + "</optional>").replace('\'', '"'));
    }

    public void testOptionalWithAlias() {
        final Optional<String> optional = Optional.of("test");
        xstream.aliasField("junit", Optional.class, "value");
        assertBothWays(optional, ("" //
            + "<optional>\n" //
            + "  <junit class='string'>test</junit>\n" //
            + "</optional>").replace('\'', '"'));
    }

    public void testOptionalIsRerenceable() {
        @SuppressWarnings("unchecked")
        final Optional<Object>[] array = new Optional[3];
        array[0] = array[2] = Optional.of("test");
        array[1] = Optional.empty();
        assertBothWays(array, ("" //
            + "<optional-array>\n" //
            + "  <optional>\n" //
            + "    <value class='string'>test</value>\n" //
            + "  </optional>\n" //
            + "  <optional/>\n" //
            + "  <optional reference='../optional'/>\n" //
            + "</optional-array>").replace('\'', '"'));
    }

    public void testOptionalWithOldFormat() {
        assertEquals(Optional.of("test"), xstream.fromXML("" //
            + "<java.util.Optional>\n" //
            + "  <value class='string'>test</value>\n" //
            + "</java.util.Optional>"));
    }

    public void testEmptyOptionalDouble() {
        final OptionalDouble optional = OptionalDouble.empty();
        assertBothWays(optional, "<optional-double></optional-double>");
    }

    public void testEmptyOptionalDoubleWithOldFormat() {
        assertEquals(OptionalDouble.empty(), xstream.fromXML("" //
            + "<java.util.OptionalDouble>\n" //
            + "  <isPresent>false</isPresent>\n" //
            + "  <value>NaN</value>\n" //
            + "</java.util.OptionalDouble>"));
    }

    public void testOptionalDouble() {
        final OptionalDouble optional = OptionalDouble.of(1.8);
        assertBothWays(optional, "<optional-double>1.8</optional-double>");
    }

    public void testOptionalDoubleIsImmutable() {
        final OptionalDouble[] array = new OptionalDouble[3];
        array[0] = array[2] = OptionalDouble.of(1.8);
        array[1] = OptionalDouble.empty();
        assertBothWays(array, "" //
            + "<optional-double-array>\n" //
            + "  <optional-double>1.8</optional-double>\n" //
            + "  <optional-double></optional-double>\n" //
            + "  <optional-double>1.8</optional-double>\n" //
            + "</optional-double-array>");
    }

    public void testOptionalDoubleWithOldFormat() {
        assertEquals(OptionalDouble.of(1.8), xstream.fromXML("" //
            + "<java.util.OptionalDouble>\n" //
            + "  <isPresent>true</isPresent>\n" //
            + "  <value>1.8</value>\n" //
            + "</java.util.OptionalDouble>"));
    }

    public void testEmptyOptionalInt() {
        final OptionalInt optional = OptionalInt.empty();
        assertBothWays(optional, "<optional-int></optional-int>");
    }

    public void testEmptyOptionalIntWithOldFormat() {
        assertEquals(OptionalInt.empty(), xstream.fromXML("" //
            + "<java.util.OptionalInt>\n" //
            + "  <isPresent>false</isPresent>\n" //
            + "  <value>0</value>\n" //
            + "</java.util.OptionalInt>"));
    }

    public void testOptionalInt() {
        final OptionalInt optional = OptionalInt.of(42);
        assertBothWays(optional, "<optional-int>42</optional-int>");
    }

    public void testOptionalIntIsImmutable() {
        final OptionalInt[] array = new OptionalInt[3];
        array[0] = array[2] = OptionalInt.of(42);
        array[1] = OptionalInt.empty();
        assertBothWays(array, "" //
            + "<optional-int-array>\n" //
            + "  <optional-int>42</optional-int>\n" //
            + "  <optional-int></optional-int>\n" //
            + "  <optional-int>42</optional-int>\n" //
            + "</optional-int-array>");
    }

    public void testOptionalIntWithOldFormat() {
        assertEquals(OptionalInt.of(42), xstream.fromXML("" //
            + "<java.util.OptionalInt>\n" //
            + "  <isPresent>true</isPresent>\n" //
            + "  <value>42</value>\n" //
            + "</java.util.OptionalInt>"));
    }

    public void testEmptyOptionalLong() {
        final OptionalLong optional = OptionalLong.empty();
        assertBothWays(optional, "<optional-long></optional-long>");
    }

    public void testEmptyOptionalLongWithOldFormat() {
        assertEquals(OptionalLong.empty(), xstream.fromXML("" //
            + "<java.util.OptionalLong>\n" //
            + "  <isPresent>false</isPresent>\n" //
            + "  <value>0</value>\n" //
            + "</java.util.OptionalLong>"));
    }

    public void testOptionalLong() {
        final OptionalLong optional = OptionalLong.of(2344556678888786L);
        assertBothWays(optional, "<optional-long>2344556678888786</optional-long>");
    }

    public void testOptionalLongIsImmutable() {
        final OptionalLong[] array = new OptionalLong[3];
        array[0] = array[2] = OptionalLong.of(2344556678888786L);
        array[1] = OptionalLong.empty();
        assertBothWays(array, "" //
            + "<optional-long-array>\n" //
            + "  <optional-long>2344556678888786</optional-long>\n" //
            + "  <optional-long></optional-long>\n" //
            + "  <optional-long>2344556678888786</optional-long>\n" //
            + "</optional-long-array>");
    }

    public void testOptionalLongWithOldFormat() {
        assertEquals(OptionalLong.of(2344556678888786L), xstream.fromXML("" //
            + "<java.util.OptionalLong>\n" //
            + "  <isPresent>true</isPresent>\n" //
            + "  <value>2344556678888786</value>\n" //
            + "</java.util.OptionalLong>"));
    }
}
