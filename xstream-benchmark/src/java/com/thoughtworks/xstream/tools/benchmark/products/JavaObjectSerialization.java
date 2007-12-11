/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. July 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.tools.benchmark.products;

import com.thoughtworks.xstream.tools.benchmark.Product;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * Standard Java Object Serialization product.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Product
 * @see ObjectOutputStream
 * @see ObjectInputStream
 */
public class JavaObjectSerialization implements Product {

    public void serialize(Object object, OutputStream output) throws Exception {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(output);
        objectOutputStream.writeObject(object);
    }

    public Object deserialize(InputStream input) throws Exception {
        ObjectInputStream objectInputStream = new ObjectInputStream(input);
        return objectInputStream.readObject();
    }

    public String toString() {
        return "Java object serialization";
    }

}
