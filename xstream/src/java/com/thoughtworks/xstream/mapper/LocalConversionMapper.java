/*
 * Copyright (C) 2007 XStream Committers.
 * Created on 06.11.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.converters.Converter;

import java.util.HashMap;
import java.util.Map;


/**
 * A Mapper for locally defined converters for a member field.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class LocalConversionMapper extends MapperWrapper {

    private final Map localConverters = new HashMap();

    /**
     * Constructs a LocalConversionMapper.
     * 
     * @param wrapped
     * @since upcoming
     */
    public LocalConversionMapper(Mapper wrapped) {
        super(wrapped);
    }

    public void registerLocalConverter(Class definedIn, String fieldName, Converter converter) {
        localConverters.put(new Field(definedIn, fieldName), converter);
    }

    public Converter getLocalConverter(Class definedIn, String fieldName) {
        return (Converter)localConverters.get(new Field(definedIn, fieldName));
    }

    private static class Field {
        private final String name;
        private final Class declaringClass;

        private Field(Class definedIn, String name) {
            this.name = name;
            this.declaringClass = definedIn;
        }

        public String getName() {
            return this.name;
        }

        public Class getDeclaringClass() {
            return this.declaringClass;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Field) {
                final Field field = (Field)obj;
                return name.equals(field.getName())
                    && declaringClass.equals(field.getDeclaringClass());
            }
            return false;
        }

        public int hashCode() {
            return name.hashCode() ^ declaringClass.hashCode();
        }

        public String toString() {
            return declaringClass.getName() + "[" + name + "]";
        }
    }
}
