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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SortableFieldKeySorter;


public class SortableFieldListTest extends AbstractAcceptanceTest {

    public void testSortsFieldOrderWithArray() {

        final SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        sorter.registerFieldOrder(MommyBear.class, new String[]{"b", "c", "a"});

        xstream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(sorter)));
        setupSecurity(xstream);
        xstream.alias("mommy", MommyBear.class);
        final MommyBear root = new MommyBear();
        root.c = "ccc";
        root.b = "bbb";
        root.a = "aaa";
        assertBothWays(root, "<mommy>\n" + "  <b>bbb</b>\n" + "  <c>ccc</c>\n" + "  <a>aaa</a>\n" + "</mommy>");
    }

    public void testSortsFieldOrderWhileUsingInheritance() {

        final SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        sorter.registerFieldOrder(BabyBear.class, new String[]{"b", "d", "c", "a"});

        xstream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(sorter)));
        setupSecurity(xstream);
        xstream.alias("baby", BabyBear.class);
        final BabyBear root = new BabyBear();
        root.c = "ccc";
        root.b = "bbb";
        root.a = "aaa";
        root.d = "ddd";
        assertBothWays(root, "<baby>\n"
            + "  <b>bbb</b>\n"
            + "  <d>ddd</d>\n"
            + "  <c>ccc</c>\n"
            + "  <a>aaa</a>\n"
            + "</baby>");
    }

    public static class MommyBear {
        protected String c, a, b;
    }

    public static class BabyBear extends MommyBear {
        @SuppressWarnings("unused")
        private String d;
    }

}
