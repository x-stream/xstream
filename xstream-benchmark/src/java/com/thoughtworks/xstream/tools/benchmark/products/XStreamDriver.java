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

package com.thoughtworks.xstream.tools.benchmark.products;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.tools.benchmark.model.FiveBean;
import com.thoughtworks.xstream.tools.benchmark.model.OneBean;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * Generic XStream product based on an arbitrary driver.
 * 
 * @see XStream
 * @see Product
 * @see HierarchicalStreamDriver
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.4
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
public class XStreamDriver implements Product {

    private final XStream xstream;
    private final String desc;

    /**
     * Create a XStream product based on a driver.
     * 
     * @param driver the driver to use for serialization/deserialization 
     * @param desc the driver description
     * 
     * @since 1.4
     */
    public XStreamDriver(HierarchicalStreamDriver driver, String desc) {
        this.xstream = new XStream(driver);
        this.xstream.registerConverter(new JavaBeanConverter(this.xstream.getMapper()) {

            public boolean canConvert(Class type) {
                return type == OneBean.class || type == FiveBean.class;
            }
            
        });
        this.desc = desc;
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.toXML(object, output);
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    public String toString() {
        return "XStream (" + desc + ")";
    }

}
