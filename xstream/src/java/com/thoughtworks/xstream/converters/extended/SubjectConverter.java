/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.converters.extended;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a {@link Subject} instance.
 * <p>
 * Note, that this Converter does only convert the contained Principals as it is done by JDK serialization, but not any
 * credentials. For other behavior you can derive your own converter, overload the appropriate methods and register it
 * in the {@link com.thoughtworks.xstream.XStream}.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.1.3
 */
public class SubjectConverter extends AbstractCollectionConverter {

    public SubjectConverter(final Mapper mapper) {
        super(mapper);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Subject.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Subject subject = (Subject)source;
        marshalPrincipals(subject.getPrincipals(), writer, context);
        marshalPublicCredentials(subject.getPublicCredentials(), writer, context);
        marshalPrivateCredentials(subject.getPrivateCredentials(), writer, context);
        marshalReadOnly(subject.isReadOnly(), writer);
    }

    protected void marshalPrincipals(final Set<Principal> principals, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        writer.startNode("principals");
        for (final Principal principal : principals) {
            writeCompleteItem(principal, context, writer);
        }
        writer.endNode();
    };

    protected void marshalPublicCredentials(final Set<Object> pubCredentials, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
    };

    protected void marshalPrivateCredentials(final Set<Object> privCredentials, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
    };

    protected void marshalReadOnly(final boolean readOnly, final HierarchicalStreamWriter writer) {
        writer.startNode("readOnly");
        writer.setValue(String.valueOf(readOnly));
        writer.endNode();
    };

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Set<Principal> principals = unmarshalPrincipals(reader, context);
        final Set<Object> publicCredentials = unmarshalPublicCredentials(reader, context);
        final Set<Object> privateCredentials = unmarshalPrivateCredentials(reader, context);
        final boolean readOnly = unmarshalReadOnly(reader);
        return new Subject(readOnly, principals, publicCredentials, privateCredentials);
    }

    protected Set<Principal> unmarshalPrincipals(final HierarchicalStreamReader reader,
            final UnmarshallingContext context) {
        return populateSet(reader, context);
    };

    protected Set<Object> unmarshalPublicCredentials(final HierarchicalStreamReader reader,
            final UnmarshallingContext context) {
        return Collections.emptySet();
    };

    protected Set<Object> unmarshalPrivateCredentials(final HierarchicalStreamReader reader,
            final UnmarshallingContext context) {
        return Collections.emptySet();
    };

    protected boolean unmarshalReadOnly(final HierarchicalStreamReader reader) {
        reader.moveDown();
        final boolean readOnly = Boolean.getBoolean(reader.getValue());
        reader.moveUp();
        return readOnly;
    };

    protected Set<Principal> populateSet(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Set<Principal> set = new HashSet<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            final Principal principal = (Principal)readCompleteItem(reader, context, set);
            set.add(principal);
        }
        reader.moveUp();
        return set;
    }
}
