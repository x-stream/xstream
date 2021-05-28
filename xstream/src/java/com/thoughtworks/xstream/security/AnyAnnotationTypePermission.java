/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 */
package com.thoughtworks.xstream.security;

import java.util.regex.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAliasType;
import com.thoughtworks.xstream.annotations.XStreamInclude;

/**
 * Permission for any type which is annotated with an XStream annotation.
 * This presumes that because the class has an XStream annotation, it was designed with XStream in mind,
 * and therefore it is not vulnerable. Jackson and JAXB follow this philosophy too.
 * 
 * @author Geoffrey De Smet
 * @since 1.5.0
 */
public class AnyAnnotationTypePermission implements TypePermission {

    @Override
    public boolean allows(final Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isAnnotationPresent(XStreamAlias.class)
                || type.isAnnotationPresent(XStreamAliasType.class)
                || type.isAnnotationPresent(XStreamInclude.class);
    }

}
