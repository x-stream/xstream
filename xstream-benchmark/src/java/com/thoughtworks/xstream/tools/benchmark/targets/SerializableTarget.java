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
import com.thoughtworks.xstream.tools.benchmark.model.SerializableFive;
import com.thoughtworks.xstream.tools.benchmark.model.SerializableOne;

import java.util.ArrayList;
import java.util.List;

/**
 * Target containing basic types using the SerializableConverter.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
public class SerializableTarget implements Target {

    private List list;
    
    public SerializableTarget() {
        list = new ArrayList();
        for (int i = 0; i < 5; ++i) {
            list.add(new SerializableOne(Integer.toString(i)));
        }
        list.add(new SerializableFive("1", 2, true, '4', new StringBuffer("5")));
    }
    
    public boolean isEqual(Object other) {
        return list.equals(other);
    }

    public Object target() {
        return list;
    }

    public String toString() {
        return "Serializable types";
    }
}
