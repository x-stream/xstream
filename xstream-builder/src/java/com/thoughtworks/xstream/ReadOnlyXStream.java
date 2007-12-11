/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 14. May 2007 by Guilherme Silveira
 */
package com.thoughtworks.xstream;

/**
 * @author Guilherme Silveira
 * @since upcoming 
 */
public class ReadOnlyXStream {

    private final XStream xstream;

    public ReadOnlyXStream(XStream xstream) {
        this.xstream = xstream;
    }

    public Object fromXML(String xml) {
        return xstream.fromXML(xml);
    }

    public String toXML(Object obj) {
        return xstream.toXML(obj);
    }
}
