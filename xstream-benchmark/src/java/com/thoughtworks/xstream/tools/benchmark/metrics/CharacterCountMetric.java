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

package com.thoughtworks.xstream.tools.benchmark.metrics;

import com.thoughtworks.xstream.tools.benchmark.Metric;
import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.tools.benchmark.Target;

import java.io.ByteArrayOutputStream;

/**
 * Determines the amount of a special characters.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
public class CharacterCountMetric implements Metric {
    
    private final char ch;

    public CharacterCountMetric(char ch) {
        this.ch = ch;
    }

    public double run(Product product, Target target) throws Exception {
        return run(product, target.target());
    }

    /**
     *@deprecated since 1.3
     */
    public double run(Product product, Object object) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        product.serialize(object, buffer);
        String s = buffer.toString();
        int counter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ch) {
                ++counter;
            }
        }
        return counter;
    }

    public String toString() {
        return "Character count for '" + ch + "'";
    }

    public String unit() {
        return "characters";
    }

    public boolean biggerIsBetter() {
        return false;
    }
}
