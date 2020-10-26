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

package com.thoughtworks.xstream.tools.benchmark.targets;

import com.thoughtworks.xstream.tools.benchmark.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Target containing basic types.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
public class BasicTarget implements Target {

    private List list;
    
    public BasicTarget() {
        list = new ArrayList();
        list.add(Integer.valueOf(1));
        list.add(Byte.valueOf((byte)2));
        list.add(Short.valueOf((short)3));
        list.add(Long.valueOf(4));
        list.add("Profile");
        list.add(Boolean.TRUE);
        list.add(Float.valueOf(1.2f));
        list.add(Double.valueOf(1.2f));
        list.add(new File("profile.txt"));
        list.add(Locale.ENGLISH);
    }
    
    public boolean isEqual(Object other) {
        return list.equals(other);
    }

    public Object target() {
        return list;
    }

    public String toString() {
        return "SingleValue Converters";
    }
}
