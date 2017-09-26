/*
 * Copyright (C) 2008, 2010, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 13. October 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

public final class FastField {
    private final String name;
    private final String declaringClass;
    private final boolean isAttribute;

    public FastField(final String definedIn, final String name) {
        this.name = name;
        declaringClass = definedIn;
        this.isAttribute = false;
    }

    public FastField(final String definedIn, final String alias, boolean isAttribute) {
        this.name = alias;
        declaringClass = definedIn;
        this.isAttribute = isAttribute;
    }
    
    public FastField(final Class<?> definedIn, final String name) {
        this(definedIn == null ? null : definedIn.getName(), name);
    }

    public String getName() {
        return name;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FastField other = (FastField) obj;
		if (declaringClass == null) {
			if (other.declaringClass != null)
				return false;
		}
		else if (!declaringClass.equals(other.declaringClass))
			return false;
		if (isAttribute != other.isAttribute)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((declaringClass == null) ? 0 : declaringClass.hashCode());
		result = prime * result + (isAttribute ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
    
    @Override
    public String toString() {
        return (declaringClass == null ? "" : declaringClass + ".") + name + " " + isAttribute ;
    }

}