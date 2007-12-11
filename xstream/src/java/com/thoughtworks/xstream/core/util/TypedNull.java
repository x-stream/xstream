/*
 * Copyright (c) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. March 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

/**
 * A placeholder for a <code>null</code> value of a specific type.
 *
 * @author  J&ouml;rg Schaible
 * @since 1.2.2
 */
public class TypedNull
{
	private final Class type;
	
	public TypedNull(Class type)
	{
		super();
		this.type = type;
	}
	
	public Class getType()
	{
		return this.type;
	}
}
