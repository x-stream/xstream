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
}
