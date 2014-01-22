/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 09. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Explicit permission for a type with a name matching one in the provided list.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class ExplicitTypePermission implements TypePermission {

    final Set<String> names;
    
    /**
     * @since upcoming
     */
    public ExplicitTypePermission(String...names) {
        this.names = names == null ? Collections.<String>emptySet() : new HashSet<String>(Arrays.asList(names));
    }

    public boolean allows(Class type) {
        if (type == null)
            return false;
        return names.contains(type.getName());
    }

}
