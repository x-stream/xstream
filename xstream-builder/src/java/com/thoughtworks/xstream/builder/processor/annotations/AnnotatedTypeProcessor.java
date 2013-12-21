/*
 * Copyright (C) 2007, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. October 2007 by Guilherme Silveira
 */
package com.thoughtworks.xstream.builder.processor.annotations;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.builder.processor.TypeConfigProcessor;


public class AnnotatedTypeProcessor implements TypeConfigProcessor {

    public void process(XStream instance, Class type) {
        instance.processAnnotations(type);
    }

}
