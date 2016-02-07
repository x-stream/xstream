/*
 * Copyright (C) 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 7. February 2016 by Aaron Johnson
 */
package com.thoughtworks.xstream.converters.extended;

import java.lang.reflect.Method;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.core.JVM;

/**
 * This converter will take care of storing and retrieving a Path.
 *
 * @author Aaron Johnson
 */
public class PathConverter extends AbstractSingleValueConverter {
	
	/** java.nio.file.Path.class. */
	private static final Class PATH_CLASS_INTERFACE;
	
	/** A system specific concrete class of java.nio.file.Path. */
	private static final Class PATH_CLASS;
	
	/** The Paths.get() method.*/
	private static final Method GET_METHOD;

	static {
		Class pathClassSpecific = null;
		Class pathClassInterface = null;
		Method getMethod = null;
		try {
			if (JVM.is17()) {
				pathClassInterface = JVM.loadClassForName("java.nio.file.Path");
				
				// call java.nio.file.Paths.get(".").getClass() to get the system specific class.
				final Class pathsClass = JVM.loadClassForName("java.nio.file.Paths");
				getMethod = pathsClass.getDeclaredMethod("get", new Class[]{String.class, String[].class});
				Object pathInstance = getMethod.invoke(null, new Object[]{".", new String[0]});
				pathClassSpecific = pathInstance.getClass();
			}			
		} catch (final Exception e) {
			// ignored. we don't support this converter.
			pathClassSpecific = null;
			pathClassInterface = null;
			getMethod = null;
			
		} finally {
			PATH_CLASS = pathClassSpecific;
			GET_METHOD = getMethod;
			PATH_CLASS_INTERFACE = pathClassInterface;
		}
	}
	
    public boolean canConvert(Class type) {
    	if (PATH_CLASS == null) {
    		return false;
    	}
        return PATH_CLASS.isAssignableFrom(type);
    }

    public Object fromString(String str) {
    	if (GET_METHOD == null) {
    		return null;
    	}
        try {
			return GET_METHOD.invoke(null, new Object[]{str, new String[0]});
		} catch (final Exception e) {
			return null;
		}
    }

    /** The Path.toString() method returns the path as a string already. */
    public String toString(Object obj) {
        return obj + "";
    }

	/**
	 * @return the system specific implementation of java.nio.file.Path
	 */
	public static Class getPathClassSystemSpecific() {
		return PATH_CLASS;
	}
	
	/**
	 * @return the system specific implementation of java.nio.file.Path
	 */
	public static Class getPathClassInterface() {
		return PATH_CLASS_INTERFACE;
	}
}
