/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. June 2006 by Guilherme Silveira
 */
package com.thoughtworks.xstream.persistence;

import java.util.Iterator;

/**
 * A key to filename and vice-versa strategy interface.
 * 
 * @author Guilherme Silveira
 */
public interface StreamStrategy {

	Iterator iterator();

	int size();

	Object get(Object key);

	Object put(Object key, Object value);

	Object remove(Object key);

}