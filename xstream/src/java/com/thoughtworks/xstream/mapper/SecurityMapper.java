/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 08. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.ForbiddenClassException;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.TypePermission;


/**
 * A Mapper implementation injecting a security layer based on permission rules for any type required in the
 * unmarshalling process.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class SecurityMapper extends MapperWrapper {

    private final List permissions;

    /**
     * Construct a SecurityMapper.
     * 
     * @param wrapped the mapper chain
     * @since 1.4.7
     */
    public SecurityMapper(final Mapper wrapped) {
        this(wrapped, (TypePermission[])null);
    }

    /**
     * Construct a SecurityMapper.
     * 
     * @param wrapped the mapper chain
     * @param permissions the predefined permissions
     * @since 1.4.7
     */
    public SecurityMapper(final Mapper wrapped, final TypePermission[] permissions) {
        super(wrapped);
        this.permissions = permissions == null //
            ? new ArrayList()
            : new ArrayList(Arrays.asList(permissions));
    }

    /**
     * Add a new permission.
     * <p>
     * Permissions are evaluated in the added sequence. An instance of {@link NoTypePermission} or
     * {@link AnyTypePermission} will implicitly wipe any existing permission.
     * </p>
     * 
     * @param permission the permission to add.
     * @since 1.4.7
     */
    public void addPermission(final TypePermission permission) {
        if (permission.equals(NoTypePermission.NONE) || permission.equals(AnyTypePermission.ANY))
            permissions.clear();
        permissions.add(0, permission);
    }

    public Class realClass(final String elementName) {
        final Class type = super.realClass(elementName);
        for (int i = 0; i < permissions.size(); ++i) {
            final TypePermission permission = (TypePermission)permissions.get(i);
            if (permission.allows(type))
                return type;
        }
        throw new ForbiddenClassException(type);
    }
}
