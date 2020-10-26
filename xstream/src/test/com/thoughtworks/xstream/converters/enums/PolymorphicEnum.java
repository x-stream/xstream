/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2013, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 06. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.enums;

enum PolymorphicEnum implements Fruit {
    A() {
        @Override
        public String fruit() {
            return "apple";
        }
    },
    B() {
        @Override
        public String fruit() {
            return "banana";
        }
    },
    C;

    @Override
    public String fruit() {
        return "unknown";
    }

    @Override
    public String toString() {
        return fruit();
    }
}
