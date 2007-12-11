/*
 * Copyright (C) 2003 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
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

public class SampleLists extends StandardObject {
    public List good = new ArrayList();
    public Collection bad = new ArrayList();

}
