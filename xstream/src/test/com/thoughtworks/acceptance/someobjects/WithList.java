/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance.someobjects;

import com.thoughtworks.acceptance.objects.StandardObject;

import java.util.ArrayList;
import java.util.List;

public class WithList extends StandardObject {

    public List things = new ArrayList();

}
