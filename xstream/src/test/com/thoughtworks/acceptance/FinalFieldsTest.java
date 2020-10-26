/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.core.JVM;


public class FinalFieldsTest extends AbstractAcceptanceTest {

    static class ThingWithFinalField extends StandardObject {
        private static final long serialVersionUID = 200405L;
        final int number = 9;
    }

    public void testSerializeFinalFieldsIfSupported() {
        xstream = new XStream(JVM.newReflectionProvider());
        setupSecurity(xstream);
        xstream.alias("thing", ThingWithFinalField.class);

        assertBothWays(new ThingWithFinalField(), ""//
            + "<thing>\n"
            + "  <number>9</number>\n"
            + "</thing>");
    }

    public void testExceptionThrownUponSerializationIfNotSupport() {
        xstream = new XStream(new PureJavaReflectionProvider());
        xstream.alias("thing", ThingWithFinalField.class);

        try {
            xstream.toXML(new ThingWithFinalField());
        } catch (final ObjectAccessException expectedException) {
            assertEquals("Invalid final field " + ThingWithFinalField.class.getName() + ".number", expectedException
                .getMessage());
        }
    }
}
