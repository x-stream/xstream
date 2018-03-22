/*
 * Copyright (C) 2003 Joe Walnes.
 * Copyright (C) 2006, 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.acceptance.objects;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SampleLists<G,B> extends StandardObject {
    private static final long serialVersionUID = 200309L;
    public List<G> good = new ArrayList<>();
    public Collection<B> bad = new ArrayList<>();

}
