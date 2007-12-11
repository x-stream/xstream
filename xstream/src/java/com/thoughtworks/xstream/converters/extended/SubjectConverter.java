/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 12. January 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import javax.security.auth.Subject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Converts a {@link Subject} instance. Note, that this Converter does only convert the contained Principals as
 * it is done by JDK serialization, but not any credentials. For other behaviour you can derive your own converter,
 * overload the appropriate methods and register it in the {@link com.thoughtworks.xstream.XStream}.
 *
 * @author J&ouml;rg Schaible
 * @since 1.1.3
 */
public class SubjectConverter extends AbstractCollectionConverter {

    public SubjectConverter(Mapper mapper) {
        super(mapper);
    }

    public boolean canConvert(Class type) {
        return type.equals(Subject.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Subject subject = (Subject) source;
        marshalPrincipals(subject.getPrincipals(), writer, context);
        marshalPublicCredentials(subject.getPublicCredentials(), writer, context);
        marshalPrivateCredentials(subject.getPrivateCredentials(), writer, context);
        marshalReadOnly(subject.isReadOnly(), writer);
    }
    
    protected void marshalPrincipals(Set principals, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.startNode("principals");
        for (final Iterator iter = principals.iterator(); iter.hasNext();) {
            final Object principal = iter.next(); // pre jdk 1.4 a Principal was also in javax.security
            writeItem(principal, context, writer);
        }
        writer.endNode();
    };
    
    protected void marshalPublicCredentials(Set pubCredentials, HierarchicalStreamWriter writer, MarshallingContext context) {
    };

    protected void marshalPrivateCredentials(Set privCredentials, HierarchicalStreamWriter writer, MarshallingContext context) {
    };
  
    protected void marshalReadOnly(boolean readOnly, HierarchicalStreamWriter writer) {
        writer.startNode("readOnly");
        writer.setValue(String.valueOf(readOnly));
        writer.endNode();
    };

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Set principals = unmarshalPrincipals(reader, context);
        Set publicCredentials = unmarshalPublicCredentials(reader, context);
        Set privateCredentials = unmarshalPrivateCredentials(reader, context);
        boolean readOnly = unmarshalReadOnly(reader);
        return new Subject(readOnly, principals, publicCredentials, privateCredentials);
    }
    
    protected Set unmarshalPrincipals(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return populateSet(reader, context);
    };
    
    protected Set unmarshalPublicCredentials(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return Collections.EMPTY_SET;
    };

    protected Set unmarshalPrivateCredentials(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return Collections.EMPTY_SET;
    };

    protected boolean unmarshalReadOnly(HierarchicalStreamReader reader) {
        reader.moveDown();
        boolean readOnly = Boolean.getBoolean(reader.getValue());
        reader.moveUp();
        return readOnly;
    };

    protected Set populateSet(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Set set = new HashSet();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            Object elementl = readItem(reader, context, set);
            reader.moveUp();
            set.add(elementl);
        }
        reader.moveUp();
        return set;
    }
}
