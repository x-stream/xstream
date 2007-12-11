/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.alias;

import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @deprecated As of 1.2, use {@link Mapper}
 */
public interface ClassMapper extends Mapper {

    /**
     * Place holder type used for null values.
     * @deprecated As of 1.2, use {@link Mapper.Null}
     */
    class Null extends Mapper.Null {}
}
