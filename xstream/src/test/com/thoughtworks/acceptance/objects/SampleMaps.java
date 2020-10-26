/*
 * Copyright (C) 2011, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 05. August 2011 by Joerg Schaible
 */
package com.thoughtworks.acceptance.objects;

import java.util.HashMap;
import java.util.Map;


public class SampleMaps<GK, GV, BK, BV> extends StandardObject {
    private static final long serialVersionUID = 201108L;
    public Map<GK, GV> good = new HashMap<>();
    public Map<BK, BV> bad = new HashMap<>();
}
